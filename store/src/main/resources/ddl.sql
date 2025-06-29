create table catalog (
                         id bigint not null auto_increment,
                         confirmed_at timestamp null,
                         deleted_at timestamp null,
                         deleted_by bigint,
                         description text,
                         name varchar(100),
                         price float,
                         quantity int(11),
                         registered_at timestamp null,
                         registered_by bigint,
                         status varchar(30),
                         store_id bigint(20),
                         updated_at timestamp null,
                         updated_by bigint,
                         primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table catalog_not_confirmed_reason (
                                              id bigint not null auto_increment,
                                              deleted_at datetime(6),
                                              reason text,
                                              catalog_id bigint(20),
                                              primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table catalog_register_information (
                                              id bigint not null auto_increment,
                                              deleted_at datetime(6),
                                              status varchar(30),
                                              catalog_id bigint(20),
                                              primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table store (
                       id bigint not null auto_increment,
                       confirmed_at timestamp null,
                       deleted_at timestamp null,
                       deleted_by bigint(20),
                       description text,
                       name varchar(200),
                       registered_at timestamp null,
                       registered_by bigint(20),
                       status varchar(30),
                       updated_at timestamp null,
                       user_id bigint(20),
                       primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table store_history (
                               id bigint not null auto_increment,
                               history text,
                               store_id bigint(20),
                               primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table store_integration (
                                   id bigint not null auto_increment,
                                   integration_platform varchar(30),
                                   platform_id text,
                                   store_id bigint(20),
                                   primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create table store_not_confirmed_reason (
                                            id bigint not null auto_increment,
                                            deleted_at datetime(6),
                                            reason text,
                                            store_id bigint(20),
                                            primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;