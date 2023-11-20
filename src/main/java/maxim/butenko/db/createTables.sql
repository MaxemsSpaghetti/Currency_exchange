create table currencies (
        id INTEGER NOT NULL,
        fullName TEXT NOT NULL,
        code TEXT NOT NULL,
        sign TEXT NOT NULL,
        PRIMARY KEY(id)
);



CREATE TABLE exchangeRates (
       id INTEGER NOT NULL,
       baseCurrencyId INTEGER UNIQUE,
       targetCurrencyId INTEGER UNIQUE,
       rate TEXT NOT NULL,
       PRIMARY KEY(id),
       FOREIGN KEY (baseCurrencyId) REFERENCES currencies (id) ON DELETE CASCADE,
       FOREIGN KEY (targetCurrencyId) REFERENCES currencies (id) ON DELETE CASCADE
);