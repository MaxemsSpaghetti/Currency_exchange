insert into currencies (fullName, code, sign)
values ('Russian Ruble', 'RUB', '₽'),
        ('United States Dollar', 'USD', '$'),
        ('Euro', 'EUR', '€'),
        ('Japanese yen', 'JPY', '¥'),
        ('Turkish lira', 'TRY', '₺'),
        ('Kazakh Tenge', 'KZT', '₸');

insert into exchangeRates (baseCurrencyId, targetCurrencyId, rate)
values (1, 3, 0.010149),
       (2, 1, 92.05),
       (2, 3, 0.936307),
       (2, 4, 150.64),
       (2, 5, 28.61),
       (2, 6, 463.65);


