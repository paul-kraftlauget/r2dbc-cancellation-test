CREATE OR ALTER PROCEDURE [dbo].[getAllUsers]
(
    @Offset INTEGER
    ,@PageSize  INTEGER
)
AS
BEGIN

    SELECT * FROM corp_customer
    ORDER BY id ASC OFFSET @Offset ROWS FETCH NEXT @PageSize ROWS ONLY
    RETURN

END
