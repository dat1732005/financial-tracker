## Asset entity (slice 1)
- Dung @Enumerated(EnumType.STRING) thay vi ORDINAL de tranh loi neu thu tu enum
  thay doi sau nay - ORDINAL luu so thu tu, de vo du lieu khi them/xoa gia tri enum.
- Them CHECK constraint o DB (ngoai validate o Java) de bao ve tinh toan ven du lieu
  ke ca khi co thao tac insert thang SQL khong qua Hibernate.
- GenerationType.IDENTITY khop voi BIGSERIAL - Hibernate khong tu sinh ID, de Postgres
  tu tang qua sequence.