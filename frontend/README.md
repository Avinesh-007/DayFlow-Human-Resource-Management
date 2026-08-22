# Dayflow Frontend

React foundation for the Dayflow Spring Boot HRMS API.

## Run

Install Node.js 20+ and npm, then run:

```bash
npm install
copy .env.example .env
npm run dev
```

The Vite server uses `http://localhost:3000`, matching the backend's default `FRONTEND_URL`. Set `VITE_API_BASE_URL` in `.env` when the API is hosted elsewhere.

## API integration reference

All API responses use the backend envelope `{ success, message, data }`. The Axios client in `src/services/api.js` adds `Authorization: Bearer <token>` from local storage and clears the client session on `401`.

| Feature | Method | Endpoint | Access |
| --- | --- | --- | --- |
| Register | POST | `/api/auth/register` | Public |
| Login | POST | `/api/auth/login` | Public |
| Verify email | GET | `/api/auth/verify-email?token=...` | Public |
| Employee dashboard | GET | `/api/dashboard/employee` | `ADMIN`, `EMPLOYEE` |
| Admin dashboard | GET | `/api/dashboard/admin` | `ADMIN` |
| Own profile | GET/PUT | `/api/employees/me` | `ADMIN`, `EMPLOYEE` |
| Employee management | GET/POST/PUT/DELETE | `/api/employees`, `/api/employees/{id}` | `ADMIN` |
| Own attendance | GET | `/api/attendance/me` | `ADMIN`, `EMPLOYEE` |
| Check in/out | POST | `/api/attendance/check-in`, `/api/attendance/check-out` | `ADMIN`, `EMPLOYEE` |
| Attendance management | GET/PUT | `/api/attendance`, `/api/attendance/{id}` | `ADMIN` |
| Apply for leave | POST | `/api/leaves` | `ADMIN`, `EMPLOYEE` |
| Own leave | GET | `/api/leaves/me`, `/api/leaves/{id}` | `ADMIN`, `EMPLOYEE` |
| Leave approvals | GET/PUT | `/api/admin/leaves`, `/pending`, `/{id}/approve`, `/{id}/reject` | `ADMIN` |
| Own payroll | GET | `/api/payroll/me` | `ADMIN`, `EMPLOYEE` |
| Payroll management | GET/POST/PUT/DELETE | `/api/admin/payroll`, `/{employeeId}`, `/{id}` | `ADMIN` |

Registration sends `role` as `EMPLOYEE` or `ADMIN`; the backend itself prefixes this with `ROLE_`. There is no backend logout endpoint, so logout removes the client-side token and session record.
