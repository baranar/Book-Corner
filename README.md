# KitapKöşem: Online Kitap İnceleme ve Puanlama Sistemi

## Proje Hakkında

KitapKöşem, kitap severlerin yeni kitaplar keşfedebileceği, favori eserlerini inceleyebileceği ve diğer kullanıcıların yorumlarını okuyup kendi görüşlerini paylaşabileceği dinamik bir web uygulamasıdır. Bu proje, temel web geliştirme prensiplerini ve MVC (Model-View-Controller) mimarisini Java Servlet, JSP ve JDBC teknolojileriyle uygulayarak, modern bir web uygulamasının nasıl inşa edildiğini göstermektedir.

Uygulama, estetik ve kullanıcı dostu bir arayüz için HTML, CSS ve Bootstrap'tan faydalanırken, arka planda güvenli ve etkin veri yönetimi için PostgreSQL veritabanı ile etkileşim kurmaktadır.

## Temel Özellikler

### Kullanıcı Yönetimi
* **Kayıt Olma ve Giriş Yapma:** Kullanıcılar kolayca yeni bir hesap oluşturabilir veya mevcut hesaplarıyla giriş yapabilirler. Giriş yapan kullanıcılara özel özellikler ve kişiselleştirilmiş bağlantılar sunulur.
* **Çıkış Yapma:** Güvenli bir şekilde oturumu sonlandırma imkanı.

### Kitap Yönetimi
* **Kitapları Listeleme ve Arama:** Tüm kullanıcılar sisteme kayıtlı kitapların listesine erişebilir. Entegre arama çubuğu sayesinde kitap başlığına veya yazara göre hızlı filtreleme yapılabilir.
* **Kitap Detaylarını Görüntüleme:** Her kitabın kendi detay sayfası bulunur. Bu sayfada kitabın açıklaması, yazarı ve diğer kullanıcılar tarafından eklenmiş tüm yorumlar ile ortalama puanı gösterilir.
* **Yeni Kitap Ekleme:** Giriş yapmış kullanıcılar, kitap başlığı, yazar adı ve açıklaması gibi temel bilgilerle sisteme yeni kitaplar ekleyebilir.
* **Kitap Düzenleme:** Yalnızca kitabı ekleyen kullanıcılar, kendi ekledikleri kitapların bilgilerini güncelleyebilirler.
* **Kitap Silme:** Kitabı ekleyen kullanıcılar, kendi kitaplarını sistemden silebilirler. Kitap silindiğinde, ona ait tüm yorumlar da otomatik olarak silinir.

### Yorum ve Puanlama Sistemi
* **Yorum ve Puan Verme:** Giriş yapmış kullanıcılar, diledikleri bir kitap için yorum yazabilir ve 1 ile 5 arasında bir puan verebilirler.
* **Yorum Düzenleme:** Kullanıcılar, daha önce ekledikleri yorumları ve puanları güncelleyebilirler.
* **Yorum Silme:** Kullanıcılar, kendi yaptıkları yorumları silebilirler.
* **Ortalama Puan Görüntüleme:** Her kitabın detay sayfasında, tüm kullanıcıların verdiği puanların ortalaması dinamik olarak hesaplanıp gösterilir.

## Kullanılan Teknolojiler

* **Frontend:** HTML5, CSS3, Bootstrap 5, JSP (JavaServer Pages)
* **Backend:** Java Servlet, JDBC (Java Database Connectivity)
* **Veritabanı:** PostgreSQL
* **Derleme Aracı:** Apache Maven
* **Web Sunucusu:** Apache Tomcat 10+
* **Mimari:** MVC (Model-View-Controller)

## Kurulum ve Çalıştırma

### Ön Gereksinimler

Projeyi yerel ortamınızda çalıştırabilmek için aşağıdaki bileşenlerin kurulu olması gerekmektedir:

* **Java Development Kit (JDK) 17** veya daha yeni bir sürüm.
* **Apache Maven:** Proje bağımlılıklarını yönetmek ve derlemek için.
* **Apache Tomcat 10.x:** Web uygulamalarını çalıştırmak için bir servlet konteyneri.
* **PostgreSQL Veritabanı Sunucusu:** Veri depolama için.
* **Bir PostgreSQL İstemcisi:** (örn. pgAdmin, DBeaver, psql) veritabanı yönetimi ve SQL komutlarını çalıştırmak için.

### Veritabanı Kurulumu

1.  **PostgreSQL Veritabanı Oluşturma:**
    ```sql
    CREATE DATABASE kitapkosem_db;
    ```
2.  **Tabloları Oluşturma:** `mybookcorner_db` veritabanına bağlanın ve aşağıdaki SQL komutlarını çalıştırarak gerekli tabloları oluşturun:

    ```sql
    -- users tablosu
    CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL, 
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- books tablosu
    CREATE TABLE books (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        author VARCHAR(255) NOT NULL,
        description TEXT,
        added_by_user_id INT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (added_by_user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    -- reviews tablosu
    CREATE TABLE reviews (
        id SERIAL PRIMARY KEY,
        book_id INT NOT NULL,
        user_id INT NOT NULL,
        rating INT CHECK (rating >= 1 AND rating <= 5) NOT NULL,
        comment TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        UNIQUE (book_id, user_id), -- Bir kullanıcının bir kitaba yalnızca bir yorum yapabilmesini sağlar
        FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
    ```

3.  **Veritabanı Bağlantısını Yapılandırma:**
    * Projenin `src/main/java/com/mybookcorner/util/DBConnection.java` dosyasını açın.
    * `DB_URL`, `DB_USER` ve `DB_PASSWORD` sabitlerini kendi PostgreSQL veritabanı bilgilerinizle güncelleyin.

    ```java
    // Örnek:
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mybookcorner_db";
    private static final String DB_USER = "sizin_veritabani_kullanici_adiniz";
    private static final String DB_PASSWORD = "sizin_veritabani_sifreniz";
    ```

### Projeyi Derleme ve Dağıtma

1.  **Projeyi Derleme:**
    * Terminalinizi projenizin kök dizinine (yani `pom.xml` dosyasının bulunduğu yere) konumlandırın.
    * Projeyi derlemek ve paketlemek için Maven komutunu çalıştırın:
        ```bash
        mvn clean install
        ```
    * Bu komut, `target/` dizini altında `MyBookCorner.war` gibi bir `.war` dosyası oluşturacaktır.

2.  **Tomcat'e Dağıtma:**
    * Oluşturulan `MyBookCorner.war` dosyasını `target/` dizininden Tomcat kurulumunuzun `webapps/` dizinine kopyalayın.
    * Tomcat sunucusunu başlatın (eğer zaten çalışmıyorsa). Tomcat, `.war` dosyasını otomatik olarak açacak ve uygulamayı dağıtacaktır.

### Uygulamaya Erişme

* Web tarayıcınızı açın ve aşağıdaki adrese gidin:
    `http://localhost:8080/MyBookCorner/`
* Uygulama otomatik olarak kitap listesi sayfasına yönlendirecektir.

## Proje Yapısı

Proje, temiz ve bakımı kolay bir yapıya sahip olmak amacıyla aşağıdaki dizin düzenini takip eder:

![image](https://github.com/user-attachments/assets/62e456af-1c8f-44e0-8427-d454a55d4043)
