// DONT DO THIS AT HOME!
const API_DOMAIN = "https://api.bearmaster.hu";
const AUTH_DOMAIN = "https://auth.bearmaster.hu";
const CLIENT_ID = "blog-public-client";
const KEY_CODE_VERIFIER = "codeVerifier";
const AUTH_TOKENS = "authTokens";
var DateTime = luxon.DateTime;

$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        if (!settings.url.startsWith("http")) { // for relative URLs only
          settings.url = API_DOMAIN + settings.url;
          if (settings.type != "GET" && settings.type != "OPTIONS" && settings.type != "HEAD") {
            xhr.setRequestHeader("X-XSRF-TOKEN", $.cookie("XSRF-TOKEN"));
          }
          let authTokens = JSON.parse(sessionStorage.getItem(AUTH_TOKENS));
          if (authTokens && authTokens.access_token) {
            xhr.setRequestHeader("Authorization", "Bearer " + authTokens.access_token);
          }
        }
    },
    xhrFields: {
      withCredentials: true
    },
    statusCode: {
      401: function() {
        let path = $.url("path");
        if (path != "/login.html") {
          let query = $.url("query");
          let returnPath = encodeURIComponent(path + (query ? "?" + query : "") );
          window.location.replace("/login.html?returnTo=" + returnPath);
        }
      }
    }
});

function fetchPosts() {
  return $.get("/posts/latest");
}

function fetchPost(id) {
  return $.get("/post/" + id);
}

function fetchCurrentUser() {
  return $.get("/me");
}

function paraRow(post) {
    const para = $("<p></p>");
    const link = $("<a></a>");
    const span = $("<span></span>");
    link.attr("href", "/post.html?id=" + post.id);
    link.text(post.title);
    span.text(DateTime.fromISO(post.createdAt).toLocaleString(DateTime.DATETIME_FULL));
    para.append(link, " ", span);
    return para;
}

function keycloakLogin() {
  sessionStorage.removeItem(AUTH_TOKENS);
  let randomValues = crypto.getRandomValues(new Uint32Array(10));
  let codeVerifier = Array.from(randomValues, (x) => x.toString(36)).join('');
  sessionStorage.setItem(KEY_CODE_VERIFIER, codeVerifier);

  let codeChallenge = $.sha256b64(codeVerifier);
  let redirectUri = `${$.url('protocol')}://${$.url("hostname")}${$.url('path')}`;

  let target = `${AUTH_DOMAIN}/realms/spring-blog/protocol/openid-connect/auth?scope=openid&response_type=code&client_id=${encodeURIComponent(CLIENT_ID)}&redirect_uri=${encodeURIComponent(redirectUri)}&code_challenge=${convertToBase64UrlEncoded(codeChallenge)}&code_challenge_method=S256`

  window.location.replace(target);
}

function keycloakCodeExchange(code) {
  let codeVerifier = sessionStorage.getItem(KEY_CODE_VERIFIER);
  if (code && codeVerifier) {
    $.post(`${AUTH_DOMAIN}/realms/spring-blog/protocol/openid-connect/token`, {
      "grant_type": "authorization_code",
      "code": code,
      "redirect_uri": `${$.url('protocol')}://${$.url("hostname")}${$.url('path')}`,
      "client_id": CLIENT_ID,
      "code_verifier": codeVerifier
    }).done(function(result){
      sessionStorage.removeItem(KEY_CODE_VERIFIER);
      result["createdAt"] = DateTime.now();
      sessionStorage.setItem(AUTH_TOKENS, JSON.stringify(result));
      window.location.replace(returnToPath());
    });
  }
}

function convertToBase64UrlEncoded(base64) {
  let result = base64.split('=')[0];
  result = result.replaceAll('+', '-');
  result = result.replaceAll('/', '_');
  return result;
}

function returnToPath() {
  let returnPath = $.url("?returnTo");
  let target;
  if (returnPath) {
    target = decodeURIComponent(returnPath);
  } else {
    target = "/";
  }
  return target;
}
