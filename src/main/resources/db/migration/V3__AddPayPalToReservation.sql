USE findlunch;

ALTER TABLE reservation
ADD used_paypal tinyint(1) not NULL,
ADD fee decimal(5,2) not NULL,
ADD pp_transaction_id varchar(36),
ADD pp_transaction_finished tinyint(1) not NULL;