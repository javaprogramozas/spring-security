## Spring Security tutorial projekt

### Az SSL tanúsítványok generálásának menete

Egy valóságosnak látszó tanúsítványláncot szeretnénk létrehozni, ahol van egy általunk létrehozott tanúsítvány 
hitelesítő szervezet (Certificate Authority - CA), aki kiállít a saját szerverünk számára egy tanúsítványt, amit a saját
aláírásával lát el.

#### 1. Saját tanúsítvány hitelesítő (CA) tanúsítvány

Készítünk egy új titkosított kulcsot, a parancs kétszer fog kérni egy tetszőleges, általunk megadott jelszót.

```
openssl genrsa -des3 -out ca.key 2048
```

A kulcs segítségével generálunk egy X.509 formátumú tanúsítványt, ami 1895 napig lesz érvényes. 
A parancs fogja kérni az előzőleg megadott jelszót a CA kulcsához.

Mindemellett fogja kérni a tanúsítvány mezőinek értékét, itt mindehová írhatunk `.`-ot, kivéve a `Common Name` mezőt,
amit érdemes egy értelmes és felismerhető értékkel feltölteni, ami a saját CA szervezetünk neve. Így a későbbiekben 
könnyebb megtalálni miután importáltuk a tanúsítványt az operációs rendszerbe.

```
openssl req -x509 -new -nodes -key ca.key -sha256 -days 1825 -out ca.pem
```

Az elkészült `ca.crt` fájlt lehet importálni az operációs rendszer megbízható tanúsítványait kezelő részébe 
(Windows esetén az Microsoft Management Console segítségéve, amelyet az `mmc` paranccsal indíthatunk.)

#### 2. Saját szerver tanúsítvány

A szerver tanúsítványhoz is szükség lesz egy titkosított kulcsra.

```
openssl genpkey -aes-256-cbc -algorithm RSA -pkeyopt rsa_keygen_bits:4096 -out server.key
```

Az ilyen módon titkosított kulcsot a Spring SSL bundle funkciója képes visszafejteni a konfigurált kulccsal.

Ezt a kulcsot nem arra használjuk, hogy közvetlenül készítsük egy tanúsítványt, hanem egy úgynevezett 
Certificate Signing Request (CSR) fájlt hozunk létre, amiben megtalálható minden információ amire egy CA-nak szüksége 
van a tanúsítvány kiállításához. Ez a `ca.crt` fájl létrehozásához hasonló kérdéseket fog feltenni, itt is a 
`Common Name` mezőt érdemes kitölteni, a többi maradhat "üresen".

A parancs futtatásához továbbá szükséges egy "extension" fájl is, ami extra infókat tartalmaz, többek között itt adhatjuk
meg a tanúsítvány alternatív neveit, azaz azokat a domain-eket amikhez a tanúsítványt kiállították, pl:

```
authorityKeyIdentifier=keyid, issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = bearmaster.hu
DNS.2 = localhost
```

Ez után pedig a tényleges szerver tanúsítványt készítő parancs (figyeljünk oda, hogy itt a CA kulcs jelszava szükséges!)

```
openssl x509 -req -in server.csr -CA ca.pem -CAkey ca.key \
-CAcreateserial -out server.crt -days 825 -sha256 -extfile server.ext
```

### Az elkészített szerver tanúsítvány használata

Az `application.properties` konfigurációs fájlban utasíthatjuk a Spring Boot alkalmazásunkat, hogy a beágyazott 
Tomcat szervert HTTPS módban indítsa, az alábbi beállításokkal:
 * `server.port`: explicit megadjuk a portot, ezesetben a 8080 helyett a 8443-at (jelezve hogy HTTPS-t a hívó félnek, 
hogy HTTPS-ról van szó, de ez csak konvenció)
 * `server.ssl.bundle`: a HTTPS működéséhez szükséges SSL tanúsítványok beállításait a Spring Boot 
[SSL Bundle](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.ssl) funkcionalitását
használja, itt a bundle nevét kell megadni. Az SSL konfigurációt közvetlenül is meg lehet adni 
([lásd itt](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver.configure-ssl.pem-files))
 * `spring.ssl.bundle.pem.bearmaster.keystore.certificate`: a `bearmaster` nevű SSL bundle tanúsítványának elérhetősége
 * `spring.ssl.bundle.pem.bearmaster.keystore.private-key`: a `bearmaster` nevű SSL bundle-ben lévő tanúsítványhoz 
tartozó privát kulcs elérhetősége
 * `spring.ssl.bundle.pem.bearmaster.keystore.private-key-password`: a `bearmaster` nevű SSL bundle privát kulcsának
feloldó jelszava (ha ez nincs megadva, a Spring úgy veszi, hogy a kulcs nincs titkosítva)

Példa beállítások:

```
server.port=8443
server.ssl.bundle=bearmaster
spring.ssl.bundle.pem.bearmaster.keystore.certificate=classpath:server.crt
spring.ssl.bundle.pem.bearmaster.keystore.private-key=classpath:server.enc.key
spring.ssl.bundle.pem.bearmaster.keystore.private-key-password=${server.key.password}
```

### Megjegyzések

Ha szeretnénk látni, mit tartalmaz egy tanúsítvány, használhatjuk az alábbi parancsot:

```
openssl x509 -text -noout -in server.crt
```

Aki Windows-os Git Bash-ben futtatja az OpenSSL-t, annak érdemes minden parancsot `winpty` paranccsal prefixelnie, 
mert az OpenSSL feltételezi, hogy egy Windows command line-ban fut, nem pedig Cygwin-ben, ezért a kimenet egyszerűen 
megakad és az OpenSSL nem adja vissza a prompt-ot.

Például: 

```
winpty openssl x509 -text -noout -in server.crt
```

Továbbá az OpenSSL direktben is futtatható egy Windows Command Line-ból, ha tudjuk a teljes elérési utat, például:

```
"C:\Program Files\Git\mingw64\bin\openssl" x509 -text -noout -in server.crt
```