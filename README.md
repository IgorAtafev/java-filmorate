# Репозиторий для проекта Filmorate

### Схема БД

<img src="db_scheme.png">  
  
------  
**film**  
Информация о фильмах:  
  
    id — идентификатор (первичный ключ);
    name — название фильма;
    description — описание фильма;
    release_date — дата релиза;
    duration — продолжительность фильма;
    mpa_id — идентификатор возрастного рейтинга (внешний ключ - отсылает к таблице mpa);

**mpa**  
Информация о возрастных рейтингах фильмов:

    id — идентификатор (первичный ключ);
    name — название возрастного рейтинга;

**genre**  
Информация о жанрах фильмов:

    id — идентификатор (первичный ключ);
    name — название жанра;

**film_genre**  
Связь фильма с жанрами:

    film_id — идентификатор фильма (первичный ключ, внешний ключ - отсылает к таблице film);
    genre_id — идентификатор жанра (первичный ключ, внешний ключ - отсылает к таблице genre);
 
**user**  
Информация о пользователях: 
  
    id — идентификатор (первичный ключ);
    email — электронная почта;
    login — логин пользователя;
    name — имя пользователя;
    birth_day — дата рождения; 

**user_friends**  
Друзья пользователя: 
  
    user_id — идентификатор пользователя (первичный ключ, внешний ключ - отсылает к таблице user);
    friend_id — идентификатор пользователя (первичный ключ, внешний ключ - отсылает к таблице user);

**film_likes**  
Лайки фильмов: 
  
    film_id — идентификатор фильма (первичный ключ, внешний ключ - отсылает к таблице film);
    user_id — идентификатор пользователя (первичный ключ, внешний ключ - отсылает к таблице user);

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

**10 наиболее популярных фильмов по количеству лайков**
```roomsql
SELECT t1.*,
       COUNT(t2.user_id) count_of_likes
FROM film t1
INNER JOIN film_likes t2 ON t2.film_id = t1.id
GROUP BY t2.film_id
ORDER BY count_of_likes DESC
LIMIT 10;
```