
    create table SAMPLE.ACCOUNT (
        ID bigint generated by default as identity,
        CREATED timestamp not null,
        CREATED_BY varchar(255) not null,
        LAST_MODIFIED timestamp not null,
        MODIFIED_BY varchar(255) not null,
        UUID varchar(255) not null unique,
        VERSION integer,
        ANNUAL_REVENUE decimal(19,2) check (ANNUAL_REVENUE>=0),
        NAME varchar(16) not null,
        NUMBER_OF_EMPLOYEES integer check (NUMBER_OF_EMPLOYEES>=0),
        ADDRESS bigint,
        CURRENCY varchar(255),
        INDUSTRY varchar(255),
        primary key (ID),
        unique (UUID)
    );

    create table SAMPLE.ACCOUNT_TYPE (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.ACCOUNT_TYPES (
        ACCOUNT bigint not null,
        TYPES varchar(255) not null,
        primary key (ACCOUNT, TYPES)
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
        TYPE varchar(255) not null,
        ZIP_CODE varchar(255),
        COUNTRY varchar(255) not null,
        STATE varchar(255),
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
        DO_NOT_CALL boolean not null,
        FIRST_NAME varchar(16) not null,
        LAST_NAME varchar(16) not null,
        COUNTRY_CODE integer,
        NUMBER bigint,
        TYPE varchar(255),
        NOTE clob,
        TITLE varchar(16),
        ACCOUNT bigint,
        ADDRESS bigint not null,
        OTHER_ADDRESS bigint,
        primary key (ID),
        unique (UUID)
    );

    create table SAMPLE.COUNTRY (
        ID varchar(255) not null,
        NAME varchar(255),
        MAX_POSTAL_CODE varchar(255),
        MIN_POSTAL_CODE varchar(255),
        TYPE varchar(255),
        primary key (ID)
    );

    create table SAMPLE.CURRENCY (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.INDUSTRY (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.OPPORTUNITY (
        ID bigint generated by default as identity,
        CREATED timestamp not null,
        CREATED_BY varchar(255) not null,
        LAST_MODIFIED timestamp not null,
        MODIFIED_BY varchar(255) not null,
        UUID varchar(255) not null unique,
        VERSION integer,
        AMOUNT decimal(19,2) check (AMOUNT>=0),
        COMMISSION float not null,
        DESCRIPTION clob,
        EXPECTED_CLOSE_DATE date,
        NAME varchar(32) not null,
        PROBABILITY float not null,
        ACCOUNT bigint,
        CURRENCY varchar(255),
        SALES_STAGE varchar(255),
        primary key (ID),
        unique (UUID)
    );

    create table SAMPLE.SALES_STAGE (
        ID varchar(255) not null,
        NAME varchar(255),
        primary key (ID)
    );

    create table SAMPLE.STATE (
        ID varchar(255) not null,
        NAME varchar(255),
        CODE varchar(255),
        TYPE varchar(255),
        COUNTRY varchar(255) not null,
        primary key (ID)
    );

    create index IDX_ACCOUNT_CURRENCY on SAMPLE.ACCOUNT (CURRENCY);

    create index IDX_ACCOUNT_ADDRESS on SAMPLE.ACCOUNT (ADDRESS);

    create index IDX_ACCOUNT_INDUSTRY on SAMPLE.ACCOUNT (INDUSTRY);

    alter table SAMPLE.ACCOUNT 
        add constraint FK_ACCOUNT_ADDRESS 
        foreign key (ADDRESS) 
        references SAMPLE.ADDRESS;

    alter table SAMPLE.ACCOUNT 
        add constraint FK_ACCOUNT_CURRENCY 
        foreign key (CURRENCY) 
        references SAMPLE.CURRENCY;

    alter table SAMPLE.ACCOUNT 
        add constraint FK_ACCOUNT_INDUSTRY 
        foreign key (INDUSTRY) 
        references SAMPLE.INDUSTRY;

    alter table SAMPLE.ACCOUNT_TYPES 
        add constraint FKD10D5BA76A1FE44A 
        foreign key (TYPES) 
        references SAMPLE.ACCOUNT_TYPE;

    alter table SAMPLE.ACCOUNT_TYPES 
        add constraint FK_ACCOUNT_TYPE 
        foreign key (ACCOUNT) 
        references SAMPLE.ACCOUNT;

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

    create index IDX_CONTACT_OTHER_ADDRESS on SAMPLE.CONTACT (OTHER_ADDRESS);

    create index IDX_CONTACT_ACCOUNT on SAMPLE.CONTACT (ACCOUNT);

    create index IDX_CONTACT_PRIMARY_ADDRESS on SAMPLE.CONTACT (ADDRESS);

    alter table SAMPLE.CONTACT 
        add constraint FK_CONTACT_OTHER_ADDRESS 
        foreign key (OTHER_ADDRESS) 
        references SAMPLE.ADDRESS;

    alter table SAMPLE.CONTACT 
        add constraint FK_CONTACT_PRIMARY_ADDRESS 
        foreign key (ADDRESS) 
        references SAMPLE.ADDRESS;

    alter table SAMPLE.CONTACT 
        add constraint FK_CONTACT_ACCOUNT 
        foreign key (ACCOUNT) 
        references SAMPLE.ACCOUNT;

    create index IDX_OPPORTUNITY_ACCOUNT on SAMPLE.OPPORTUNITY (ACCOUNT);

    create index IDX_OPPORTUNITY_SALES_STAGE on SAMPLE.OPPORTUNITY (SALES_STAGE);

    create index IDX_OPPORTUNITY_CURRENCY on SAMPLE.OPPORTUNITY (CURRENCY);

    alter table SAMPLE.OPPORTUNITY 
        add constraint FK_OPPORTUNITY_CURRENCY 
        foreign key (CURRENCY) 
        references SAMPLE.CURRENCY;

    alter table SAMPLE.OPPORTUNITY 
        add constraint FK_OPPORTUNITY_SALES_STAGE 
        foreign key (SALES_STAGE) 
        references SAMPLE.SALES_STAGE;

    alter table SAMPLE.OPPORTUNITY 
        add constraint FK_OPPORTUNITY_ACCOUNT 
        foreign key (ACCOUNT) 
        references SAMPLE.ACCOUNT;

    create index IDX_STATE_COUNTRY on SAMPLE.STATE (COUNTRY);

    alter table SAMPLE.STATE 
        add constraint FK_STATE_COUNTRY 
        foreign key (COUNTRY) 
        references SAMPLE.COUNTRY;
