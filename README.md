# Webshop
English description
for Hungarian, check below

Hungarian description
Ez a backend tartalmaz egy Springboot API-t amin keresztül lehet regisztrálni, bejelentkezni, és validálni a kéréseket felhasználó rang alapján (role-based authentication).
Az webshop Itemek megtekintéséhez nem kell authentikáció. A webshop itemeket az admin felhasználók tudnak készíteni (Lehet már bővítettem a jövőben többrétegű jogosultságrendszerrel).
A Security Backbone az a https://github.com/ali-bouali/spring-boot-3-jwt-security projekt alapján lett elkészítve, ezt használtam "váznak" és bővítettem extra check-ekkel.
A backend Bearer token-es validációt hajt végre a backend minden olyan kérésére amelyhez kell authentikáció. A tokenek egy napig érvényesek, 7 napig érvényes egy refreshtoken.

Ezt a részt bővíteni a shoppal, eddig még csak a felhasználók vannak meg



