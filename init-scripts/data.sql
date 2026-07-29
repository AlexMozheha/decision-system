
create table if not exists role (
    role_id integer primary key,
    role_name varchar(30) not null,
    description text);

create table if not exists status (
    status_id integer primary key,
    name varchar(30) not null,
    description text);

create table if not exists calculation_method (
    method_id integer primary key,
    name varchar(50) not null,
    description text);



insert into role (role_id, role_name) values (1, 'INVESTOR') on conflict do nothing;
insert into role (role_id, role_name) values (2, 'ADMIN') on conflict do nothing;

insert into status (status_id, name) values (1, 'DRAFT') on conflict do nothing;
insert into status (status_id, name) values (2, 'READY_FOR_CALCULATION') on conflict do nothing;
insert into status (status_id, name) values (3, 'CALCULATING') on conflict do nothing;
insert into status (status_id, name) values (4, 'CALCULATED') on conflict do nothing;
insert into status (status_id, name) values (5, 'CALCULATION_FAILED') on conflict do nothing;

insert into calculation_method (method_id, name) values (1, 'METHOD_WS_RISK') on conflict do nothing;
insert into calculation_method (method_id, name) values (2, 'METHOD_OTHER') on conflict do nothing;


SELECT setval(pg_get_serial_sequence('role', 'role_id'), COALESCE(max(role_id), 1)) FROM role;
SELECT setval(pg_get_serial_sequence('status', 'status_id'), COALESCE(max(status_id), 1)) FROM status;
SELECT setval(pg_get_serial_sequence('calculation_method', 'method_id'), COALESCE(max(method_id), 1)) FROM calculation_method;