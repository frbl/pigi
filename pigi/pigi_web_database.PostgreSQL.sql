
START TRANSACTION ISOLATION LEVEL SERIALIZABLE, READ WRITE;

CREATE SCHEMA pigi;

SET search_path TO PIGI,"$user",public;

CREATE DOMAIN pigi.Number AS INTEGER CONSTRAINT Number_Unsigned_Chk CHECK (VALUE >= 0);

CREATE DOMAIN pigi.AverageComplexity AS INTEGER CONSTRAINT AverageComplexity_Unsigned_Chk CHECK (VALUE >= 0);

CREATE TABLE pigi.Repository
(
	URL CHARACTER VARYING NOT NULL,
	description CHARACTER VARYING NOT NULL,
	name CHARACTER VARYING NOT NULL,
	CONSTRAINT Repository_PK PRIMARY KEY(URL)
);

CREATE TABLE pigi.Revision
(
	number pigi.Number NOT NULL,
	repository CHARACTER VARYING NOT NULL,
	averageComplexity pigi.AverageComplexity NOT NULL,
	CONSTRAINT Revision_PK PRIMARY KEY(number, repository)
);

ALTER TABLE pigi.Revision ADD CONSTRAINT Revision_FK FOREIGN KEY (repository) REFERENCES pigi.Repository (URL) ON DELETE RESTRICT ON UPDATE RESTRICT;

COMMIT WORK;
