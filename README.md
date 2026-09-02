💳 PaySphere - Backend 

PaySphere is a secure payment processing backend built using Java and Spring Boot, providing user authentication, payment processing, transaction management, and Redis-based fraud detection.

✨ Features

- 🔐 User Registration & Login
- 🔑 JWT-based Authentication
- 💳 Payment Processing
- 📜 Transaction History
- 🛡️ Redis-based Fraud Detection
- ⚡ Rate Limiting for Excessive Payment Attempts
- 🗄️ MySQL Database
- 🌐 RESTful APIs
- ⚠️ Global Exception Handling

🛡️ Fraud Detection

Redis tracks payment attempts based on card and IP address.

- 💳 More than  5 attempts from the same card within 5 minutes → payment blocked
- 🌐 More than  10 attempts from the same IP within 5 minutes → payment blocked
- ⏱️ Redis counters automatically expire after 5 minutes

This helps prevent excessive payment attempts and potentially fraudulent transactions.

🛠️ Tech Stack

- ☕ Java
- 🌱 Spring Boot
- 🔐 Spring Security & JWT
- 🗄️ MySQL
- ⚡ Redis
- 🌐 REST APIs
- 📦 Maven

## 🔄 Working Flow

## 🔄 Working Flow

👤 User <br>
   ↓ <br>
🔐 Register / Login <br>
↓ <br>
🔑 JWT Token Generated <br>
↓ <br>
💳 Payment Request <br>
↓ <br>
🛡️ Fraud Check using Redis <br>
↓ <br>
⚡ Check Card & IP Attempt Limits <br>
↓ <br>
❌ Limit Exceeded → Payment Blocked <br>
↓ <br>
✅ Within Limit <br>
↓ <br>
💳 Payment Processed <br>
↓ <br>
🗄️ Payment Saved in MySQL <br>
↓ <br>
📜 Transaction History

🌐 Live Links

🖥️ Frontend: https://paysphere-frontend.vercel.app

⚙️ Backend: https://paysphere-backend-production.up.railway.app
  
