CREATE OR ALTER PROCEDURE [dbo].[insertManyUsers]
(
    @json NVARCHAR
)
AS
BEGIN

    INSERT INTO master.dbo.corp_customer
    SELECT *
    FROM OPENJSON(@json)
      WITH (
        id INT 'strict $.id',
        userId VARCHAR(45) '$.userId'
      );

END
