INSERT INTO MEMBER (UID, LOGIN_ID, PW, NAME, EMAIL, PHONE, ACTIVATED) VALUES (1, 'admin_id', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin_name', 'test1@gmail.com', '010-1234-1234', 1);
INSERT INTO MEMBER (UID, LOGIN_ID, PW, NAME, EMAIL, PHONE, ACTIVATED) VALUES (2, 'user_ud', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'user_name', 'test2@gmail.com', '010-1234-5678', 1);

INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_ADMIN');
INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_USER');

INSERT INTO MEMBER_AUTHORITY (MEMBER_ID, MEMBER_TYPE) values (1, 'ROLE_ADMIN');
INSERT INTO MEMBER_AUTHORITY (USER_ID, MEMBER_TYPE) values (2, 'ROLE_USER');