# URL-Shortener

A secure and scalable URK-Shortener Rest API built with Java, MySQL, Redis, Hibernate and SpringBoot.
This application converts long url's into short and sharable url. And provides fast retrieval using Redis cache. Redis cache make application fast and save more time.
Also use JWT authentication for secure login.

# Features
1. Short URL create from long url's
2. Short URL redirect to long URL website
3. User registration and authentication
4. JWT authentication
5. Using BCrypt, Password hashing
6. URL management by individual users
7. URL expiration after given time
8. Tracking number of clicks on URL
9. Redis caching for fast resolution of URLs
10. Use MySQL database for storage
11. Global exception handling
12. RESTful API design

# Tech Stack

## Backend
1. Java 17
2. Spring Boot
3. Spring Web
4. Spring Data JPA
5. Spring Security
6. JWT
7. Hibernate

## Database & Caching
1. MySQL
2. Redis

## Development
1. Maven
2. Postman
3. Git & GitHub

# Architecture
Note:- The architecture of the project is organized into feature packages
The packages is:

src/main/java/com/abhay/urlshortener
│
|── auth
│   ├── controller
│   ├── dto
│   ├── mapper
│   ├── repository
│   └── service
│
|── url
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
|── common
    |── config
    |── entity
    |── exception
    |── security




# URL Resolution Flow
  
  Client
  │
GET /{shortCode}
  │
Redis Cache
  │
  ├─ Cache Hit ─ Return Original URL
  │
  |── Cache Miss
          │
       MySQL
          │
    Validate Expiry
          │
    Store in Redis
          │
    Return Original URL


# Authentication
  ## Authentication endpoints:
  POST /api/v1/auth/register
  POST /api/v1/auth/login

  ## Protected endpoint require:
  Authorization: Bearer <JWT_TOKEN>


# Redis Caching
  Redis is used as a cache

  This application follows a cache-side approach:
  1. Check Redis.
  2. If the URL exists, return url.
  3. If if does not exists, retrieve it from MySQL.
  4. Validate URL expiration.
  5. Store the URL in Redis.
  6. Return the original URL.

# Database
This application uses MySQL for persistence storage.
Main entity is: ShortUrl
Relationship is: 1 to many (A single user can store multiple short Url).

# Configuration
Create environment variables for:

1. DB_USERNAME=root
2. DB_PASSWORD=your_mysql_password
3. REDIS_HOST=localhost
4. REDIS_PORT=6379
5. JWT_SECRET=your_secret_key
6. JWT_EXPIRATION=3600000
7. SERVER_PORT=8080

# Runnig the Project
## Prerequisits
Following tool need to installed:
1. Java 17
2. Maven
3. MySQL 8+
4. Redis
5. Git

# Future Improvements
1. URL analytics dashboard
2. Advanced click analytics
3. Rate Limiting
4. Custom short codes
5. QR code generation
6. Docker support
7. API documentation with Swagger/OpenAPI
8. Production deployment

# 👨‍💻 Author
Abhay Kumar
GitHub: https://github.com/Abhaykm708
LinkedIn: https://linkedin.com/in/abhay-kumar7
LeetCode: https://leetcode.com/Abhaykm70

# 📄 License
This project is licensed under the MIT License.t
