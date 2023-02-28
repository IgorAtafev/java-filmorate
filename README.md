# java-filmorate
Репозиторий для проекта Filmorate.  
  
<img src="db_scheme.png">  
  
------  
**film**  
Содержит информацию о фильмах.  
Таблица состоит из полей:  
  
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
Содержит информацию о пользователях.  
Таблица состоит из полей: 
  
    id — идентификатор (первичный ключ);
    email — электронная почта;
    login — логин;
    name — имя;
    birth_day — дата рождения;
  
