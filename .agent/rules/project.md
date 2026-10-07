\---

trigger: always\_on

\---



\# Quy tac cho project financial-tracker



\- Doc docs/REQUIREMENTS.md truoc khi de xuat bat ky thay doi nao.

\- Moi lan chi lam dung pham vi duoc giao trong prompt, khong tu them tinh nang,

&#x20; khong refactor code ngoai pham vi.

\- Luon dung Flyway cho thay doi schema (file trong src/main/resources/db/migration/),

&#x20; khong de Hibernate auto-generate DDL.

\- Composite UNIQUE constraint (asset\_id, price\_time) tren bang asset\_price.

\- Khong hardcode API key - luon doc tu bien moi truong.

\- Sau khi sinh code, liet ke ro cac file da tao/sua va ly do.

\- Neu khong chac mot quyet dinh thiet ke, hoi lai thay vi tu quyet.

\- Viet comment tieng Viet trong code cho nhung dong/doan quan trong: annotation

&#x20; la (vd @ManyToOne, @Transactional), quyet dinh thiet ke (vd vi sao dung Instant

&#x20; thay vi LocalDateTime), va logic nghiep vu khong hien nhien. Khong comment nhung

&#x20; dong da ro nghia qua ten bien/method.

