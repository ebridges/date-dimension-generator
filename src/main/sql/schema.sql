drop table t_holidays;
drop table t_dates;

-- http://dbdsgnr.appspot.com/app#agdkYmRzZ25ycg4LEgZTY2hlbWEYu_ddDA

CREATE TABLE t_dates
(
  date_id integer,
  year smallint,
  month_of_year smallint,
  day_of_month smallint,
  day_of_year smallint,
  day_of_week smallint,
  quarter smallint,
  yyyy_mm_dd char(10),
  yyyy_mm char(8),
  quarter_name char(2),
  month_name char(16),
  day_name char(16),
  is_weekend bit,
  CONSTRAINT t_dates_pk PRIMARY KEY (date_id)
) WITH (
  OIDS=FALSE
);

CREATE TABLE t_holidays
(
  holiday_id char,
  date_id integer,
  country char(2),
  name character varying(64),
  is_bank_holiday bit,
  is_actual bit,
  is_observed bit,
  CONSTRAINT t_holidays_pk PRIMARY KEY (holiday_id)
) WITH (
  OIDS=FALSE
);


ALTER TABLE t_holidays ADD CONSTRAINT t_holidays_fk1 FOREIGN KEY (date_id) REFERENCES t_dates(date_id);

