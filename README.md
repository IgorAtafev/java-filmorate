# java-filmorate
Репозиторий для проекта Filmorate.  
  
<img src="db_scheme.png">  
  
------  
**film**  
Содержит информацию о фильмах:  
  
    id — идентификатор (первичный ключ);
    name — название;
    description — описание;
    release_date — дата релиза;
    duration — продолжительность;
    mpa — возрастной рейтинг:
        G — у фильма нет возрастных ограничений,
        PG — детям рекомендуется смотреть фильм с родителями,
        PG-13 — детям до 13 лет просмотр не желателен,
        R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
        NC-17 — лицам до 18 лет просмотр запрещён.
 
**user**  
Содержит информацию о пользователях: 
  
    id — идентификатор (первичный ключ);
    email — электронная почта;
    login — логин;
    name — имя;
    birth_day — дата рождения; 

**user_friends**  
Содержит id друзей пользователя: 
  
    user_id — идентификатор пользователя (первичный ключ, внешний ключ - отсылает к таблице user);
    friend_id — идентификатор пользователя (первичный ключ, внешний ключ - отсылает к таблице user);

### Примеры запросов  
**Все фильмы**
```roomsql
SELECT * 
FROM film;
```

**Фильм по id**
```roomsql
SELECT * 
FROM film
WHERE id = 1;
```

**Все пользователи**
```roomsql
SELECT * 
FROM user;
```

**Пользователь по id**
```roomsql
SELECT * 
FROM user
WHERE id = 1;
```

**Друзья пользователя**
```roomsql
SELECT t1.* 
FROM user t1
INNER JOIN user_friends t2 ON t2.friend_id = t1.id
WHERE t2.user_id = 1;
```

**Общие друзья пользователей**
```roomsql
SELECT t1.* 
FROM user t1
INNER JOIN user_friends t2 ON t2.friend_id = t1.id
INNER JOIN user_friends t3 ON t3.friend_id = t2.friend_id
WHERE t2.user_id = 1 AND t3.user_id = 2;
```