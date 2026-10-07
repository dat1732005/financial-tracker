## Asset entity (slice 1)
- Dung @Enumerated(EnumType.STRING) thay vi ORDINAL de tranh loi neu thu tu enum
  thay doi sau nay - ORDINAL luu so thu tu, de vo du lieu khi them/xoa gia tri enum.
- Them CHECK constraint o DB (ngoai validate o Java) de bao ve tinh toan ven du lieu
  ke ca khi co thao tac insert thang SQL khong qua Hibernate.
- GenerationType.IDENTITY khop voi BIGSERIAL - Hibernate khong tu sinh ID, de Postgres
  tu tang qua sequence.

## AssetPrice API & Service (slice 3)
- Tach rieng cac derived query methods trong AssetPriceRepository (Between, GreaterThanEqual,
  LessThanEqual, findByAssetCode) thay vi dung cau dynamic query co "OR :param IS NULL".
  Dieu nay giup PostgreSQL luon su dung duoc composite index tren (asset_id, price_time) va tranh full table scan.
- Dung @Transactional(readOnly = true) o tang service de bao cho Hibernate bo qua dirty checking,
  tiet kiem bo nho va CPU cho cac truy van doc.
- Dung Java record cho DTO AssetPriceResponse vi tinh bat bien (immutability), cu phap ngan gon,
  va duoc ho tro tot boi Jackson trong Java 21.
- Kiem tra su ton tai cua ma tai san qua existsByCode truoc khi truy van gia: neu khong ton tai thi
  nem ResourceNotFoundException de GlobalExceptionHandler tra ve loi HTTP 404 ro rang cho client,
  tranh tra ve HTTP 200 voi danh sach rong gay hieu nham.

## CollectionLog entity & migration (slice 4)
- Dung enum CollectionStatus (SUCCESS, FAILED) voi @Enumerated(EnumType.STRING) va them CHECK constraint
  o DB tren cot status de dong bo voi thiet ke bang asset va ngan ngua du lieu sai lech neu thu tu enum bi thay doi.
- Dung Instant o Java va TIMESTAMPTZ o DB cho cot run_at de luu thoi gian theo chuan UTC va khop voi che do validate cua Hibernate.
- Dung kieu TEXT cho cot message de co the luu tru log loi chi tiet (stack trace) ma khong bi gioi han do dai.
