CREATE DATABASE Restaurant;
use Restaurant;
CREATE TABLE seats(
s_id CHAR(3) NOT NULL PRIMARY KEY,
s_capacity INT);

CREATE TABLE foods(
f_id CHAR(5) NOT NULL PRIMARY KEY,
f_name VARCHAR(64),
f_price INT);

CREATE TABLE order_basic(
o_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
o_date DATETIME,
o_s_id CHAR(3),
o_state INT NOT NULL DEFAULT 0);

CREATE TABLE order_detail(
od_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
od_o_id INT,
od_f_id CHAR(5),
od_quantity INT,
od_memo VARCHAR(64),
od_time DATETIME, 
od_state INT NOT NULL DEFAULT 0);

/*初期データ*/
INSERT INTO seats VALUES
('001',4),
('002',2),
('003',8),
('004',6),
('005',6);

INSERT INTO foods VALUES
('00001','ビール中生',490),
('00002','ビール大生',750),
('00003','ビール小生',390),
('00004','ビール中瓶',520),
('00005','ノンアルコールビール小瓶',330),
('00006','酎ハイ',290),
('10001','枝豆',290),
('10002','板わさ',290),
('10003','漬物',290),
('10004','寄せ豆腐',390),
('30001','焼き鳥盛合せ（タレ ・ 塩）',290),
('30002','ねぎま串（タレ ・ 塩）',290),
('30003','砂肝串（タレ ・ 塩）',290),
('30004','皮串（タレ ・ 塩）',290),
('30005','ハツ串（タレ ・ 塩）',290),
('30006','ぼんちり串（タレ ・ 塩）',290),
('30007','むね肉串（タレ ・ 塩）',290),
('30008','手羽串（タレ ・ 塩）',390),
('30009','串（タレ ・ 塩）',290),
('30010','黄身つくね',290),
('30011','ねぎ塩つくね',290),
('30012','チーズinつくね',290);

INSERT INTO order_basic VALUES(1,'2023-08-20 13:00:00','001',0);

