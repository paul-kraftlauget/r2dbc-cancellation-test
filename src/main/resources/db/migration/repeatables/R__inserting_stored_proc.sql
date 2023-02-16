CREATE OR ALTER PROCEDURE [dbo].[insertManyUsers]
(
    @json NVARCHAR(MAX)
)
AS
BEGIN

    INSERT INTO master.dbo.corp_customer
    SELECT *
    FROM OPENJSON(@json)
      WITH (
        id INT '$.id',
        userId VARCHAR(45) '$.userId'
      );

END
