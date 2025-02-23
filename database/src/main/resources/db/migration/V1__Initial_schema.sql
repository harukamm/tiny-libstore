CREATE TABLE author (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_day DATE NOT NULL
);

CREATE TABLE book (
    id INT NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INT CHECK (price >= 0),
    published_status SMALLINT DEFAULT '0'
);

CREATE TABLE book_authors (
    book_id INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE
);
