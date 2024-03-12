// DONT DO THIS AT HOME!
const API_DOMAIN = "https://api.bearmaster.hu"
var DateTime = luxon.DateTime;

$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        settings.url = API_DOMAIN + settings.url;
        if (settings.type != "GET" && settings.type != "OPTIONS" && settings.type != "HEAD") {
          xhr.setRequestHeader("X-XSRF-TOKEN", $.cookie("XSRF-TOKEN"));
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

function logout() {
  return $.post("/logout");
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
