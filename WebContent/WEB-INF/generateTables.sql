CREATE TABLE CUSTOMERS
(
EMAIL CHAR(60) PRIMARY KEY,
FIRSTNAME CHAR(30) NOT NULL,
LASTNAME CHAR(30) NOT NULL);


insert into Customers(EMAIL,FIRSTNAME,LASTNAME) values 
('Has','alex','jo'),
('Wifi','lib','Par'),
('Waji','wajat','khana'),
('sam','hassam','Malik')
;


CREATE TABLE ROOMS
(ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
TYPE CHAR(15) NOT NULL,
NUMBEDS INT NOT NULL,
PRICE INT NOT NULL);





CREATE TABLE CITIES
(ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
CITY CHAR(9));




CREATE TABLE STAFF
(ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
USERNAME VARCHAR(60),
PASSWORD VARCHAR(60)
);

CREATE TABLE Owner
(ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
USERNAME VARCHAR(60),
PASSWORD VARCHAR(60)
);
insert into owner (username,password) values ('malik','malik');

CREATE TABLE BOOKINGS
(ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
CHECKIN BIGINT NOT NULL,
CHECKOUT BIGINT NOT NULL,
CUSTID CHAR(60) NOT NULL CONSTRAINT custref REFERENCES CUSTOMERS(EMAIL),
CITYID INT NOT NULL CONSTRAINT cityref REFERENCES CITIES(ID),
ROOMID INT NOT NULL CONSTRAINT roomref REFERENCES ROOMS(ID),
EXTRABED BOOLEAN,
CARDNUM BIGINT NOT NULL,
PIN INT NOT NULL,
FAIR INT
);

CREATE TABLE DISCOUNT
(
ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
STARTDATE BIGINT NOT NULL,
ENDDATE BIGINT NOT NULL,
ROOMTYPE VARCHAR(20),
DISCOUNTRATE INT
);


CREATE TABLE RESERVED
(ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
ROOMID INT NOT NULL CONSTRAINT roomref1 REFERENCES ROOMS(ID),
CITYID INT NOT NULL CONSTRAINT cityref1 REFERENCES CITIES(ID),
CUSTID CHAR(60) NOT NULL CONSTRAINT custref1 REFERENCES CUSTOMERS(EMAIL)
)
;

CREATE TABLE available
(
ROOMID INT NOT NULL CONSTRAINT roomref2 REFERENCES ROOMS(ID),
CITYID INT NOT NULL CONSTRAINT cityref2 REFERENCES CITIES(ID)
)
;



insert into avalable (cityid,roomid) values (1,1),(1,2)

insert into Bookings (Checkin,checkout,custid,cityid,roomid,extrabed,cardnum,pin,fair) values 
(08052014,31062014,'sam',1,1,true,001000,412,60),
(09102014,25122014,'Has',2,36,true,001000,412,60),
(14082014,06092014,'Wifi',3,32,true,001000,412,60),
(14082014,06092014,'Waji',1,2,true,001000,412,60),
(08052014,31062014,'sam',1,3,true,001000,412,60)
;

 
SELECT * FROM Cities c, ROOMS r WHERE Not exists (SELECT * FROM BOOKINGS where r.id=roomid and c.id=Cityid) ;

Select Checkin,checkout,c.Firstname,t.city,r.type,extrabed,cardnum,pin,fair from Bookings b, Customers c, Rooms r, Cities t where b.custid=c.EMAIL and b.cityid=t.id and b.roomid=r.id;
Select Count(*) as totalbooked from Bookings b, Customers c, Rooms r, Cities t where b.custid=c.EMAIL and b.cityid=t.id and b.roomid=r.id and t.City=Sydney AND r.type=Single;

Select id,type,Numbeds,Price from Rooms where type='Single' and id not in(select ROOMID from Reserved);



INSERT INTO CITIES (CITY) VALUES
('Sydney'),('Brisbane'),('Melbourne'),('Adelaide'),('Hobart');

INSERT INTO ROOMS(TYPE, NUMBEDS, PRICE)
VALUES ('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Single',1,70),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Twin Bed',2,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Queen',1,120),
('Executive',1,180),
('Executive',1,180),
('Executive',1,180),
('Executive',1,180),
('Executive',1,180),
('Suite',2,300),
('Suite',2,300);