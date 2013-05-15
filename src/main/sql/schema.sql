
CREATE TABLE dp_master.t_dates
(
  date_id integer,
  year smallint,
  month_of_year smallint,
  day_of_month smallint,
  day_of_year smallint,
  day_of_week smallint,
  quarter smallint,
  yyyy_mm_dd varchar(10),
  yyyy_mm varchar(8),
  quarter_name varchar(2),
  month_name varchar(16),
  day_name varchar(16),
  is_weekend boolean,
  CONSTRAINT t_dates_pk PRIMARY KEY (date_id)
) WITH (
  OIDS=FALSE
);

CREATE TABLE dp_master.t_holidays
(
  date_id integer,
  country char(2),
  name character varying(64),
  is_bank_holiday boolean,
  is_actual boolean,
  is_observed boolean
) WITH (
  OIDS=FALSE
);


ALTER TABLE t_holidays ADD CONSTRAINT t_holidays_fk1 FOREIGN KEY (date_id) REFERENCES t_dates(date_id);

