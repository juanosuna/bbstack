
    create table SAMPLE.ACCOUNT (
        ID bigint generated by default as identity,
        CREATED timestamp not null,
        CREATED_BY varchar(255) not null,
        LAST_MODIFIED timestamp not null,
        MODIFIED_BY varchar(255) not null,
        UUID varchar(255) not null unique,
        VERSION integer,
        ANNUAL_REVENUE decimal(19,2) check (ANNUAL_REVENUE>=0),
        ANNUAL_REVENUE_CURRENCY varchar(255),
        NAME varchar(16) not null,
        NUMBER_OF_EMPLOYEES integer check (NUMBER_OF_EMPLOYEES>=0),
        ADDRESS bigint,
        INDUSTRY varchar(255),
        TYPE varchar(255),
        primary key (ID),
        unique (UUID)
    );

    create table SAMPLE.ACCOUNT_TYPE (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.ADDRESS (
        ID bigint generated by default as identity,
        CREATED timestamp not null,
        CREATED_BY varchar(255) not null,
        LAST_MODIFIED timestamp not null,
        MODIFIED_BY varchar(255) not null,
        UUID varchar(255) not null unique,
        VERSION integer,
        CITY varchar(16) not null,
        STREET varchar(16) not null,
        ZIP_CODE varchar(255),
        COUNTRY varchar(255) not null,
        STATE varchar(255) not null,
        primary key (ID),
        unique (UUID)
    );

    create table SAMPLE.CONTACT (
        ID bigint generated by default as identity,
        CREATED timestamp not null,
        CREATED_BY varchar(255) not null,
        LAST_MODIFIED timestamp not null,
        MODIFIED_BY varchar(255) not null,
        UUID varchar(255) not null unique,
        VERSION integer,
        BIRTH_DATE date,
        FIRST_NAME varchar(16) not null,
        LAST_NAME varchar(16) not null,
        SOCIAL_SECURITY_NUMBER varchar(9),
        TITLE varchar(16),
        ACCOUNT bigint,
        ADDRESS bigint,
        primary key (ID),
        unique (UUID)
    );

    create table SAMPLE.COUNTRY (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.INDUSTRY (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.STATE (
        ID varchar(255) not null,
        NAME varchar(255),
        COUNTRY varchar(255) not null,
        primary key (ID)
    );

    create index IDX_ACCOUNT_TYPE on SAMPLE.ACCOUNT (TYPE);

    create index IDX_ACCOUNT_ADDRESS on SAMPLE.ACCOUNT (ADDRESS);

    create index IDX_ACCOUNT_INDUSTRY on SAMPLE.ACCOUNT (INDUSTRY);

    alter table SAMPLE.ACCOUNT 
        add constraint FK_ACCOUNT_ADDRESS 
        foreign key (ADDRESS) 
        references SAMPLE.ADDRESS;

    alter table SAMPLE.ACCOUNT 
        add constraint FK_ACCOUNT_TYPE 
        foreign key (TYPE) 
        references SAMPLE.ACCOUNT_TYPE;

    alter table SAMPLE.ACCOUNT 
        add constraint FK_ACCOUNT_INDUSTRY 
        foreign key (INDUSTRY) 
        references SAMPLE.INDUSTRY;

    create index IDX_ADDRESS_COUNTRY on SAMPLE.ADDRESS (COUNTRY);

    create index IDX_ADDRESS_STATE on SAMPLE.ADDRESS (STATE);

    alter table SAMPLE.ADDRESS 
        add constraint FK_ADDRESS_STATE 
        foreign key (STATE) 
        references SAMPLE.STATE;

    alter table SAMPLE.ADDRESS 
        add constraint FK_ADDRESS_COUNTRY 
        foreign key (COUNTRY) 
        references SAMPLE.COUNTRY;

    create index IDX_CONTACT_ADDRESS on SAMPLE.CONTACT (ADDRESS);

    create index IDX_CONTACT_ACCOUNT on SAMPLE.CONTACT (ACCOUNT);

    alter table SAMPLE.CONTACT 
        add constraint FK_CONTACT_ADDRESS 
        foreign key (ADDRESS) 
        references SAMPLE.ADDRESS;

    alter table SAMPLE.CONTACT 
        add constraint FK_CONTACT_ACCOUNT 
        foreign key (ACCOUNT) 
        references SAMPLE.ACCOUNT;

    create index IDX_STATE_COUNTRY on SAMPLE.STATE (COUNTRY);

    alter table SAMPLE.STATE 
        add constraint FK_STATE_COUNTRY 
        foreign key (COUNTRY) 
        references SAMPLE.COUNTRY;
