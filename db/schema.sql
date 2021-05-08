CREATE TABLE post (
                      id SERIAL PRIMARY KEY,
                      name TEXT
);
CREATE TABLE photo (
                       id SERIAL PRIMARY KEY,
                       name TEXT
);
CREATE TABLE candidate (
                           id SERIAL PRIMARY KEY,
                           name TEXT,
                           photo_id int NULL,
                           foreign key (photo_id)  references photo(id)
);