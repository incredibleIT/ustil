export interface IUser {
  id: number
  username: string
  email: string
  role: 'ROLE_PROBATION' | 'ROLE_MEMBER' | 'ROLE_ADMIN'
  avatar?: string
  bio?: string
  createdAt: string
}

export interface ILoginForm {
  email: string
  password: string
  rememberMe?: boolean
}

export interface IRegisterForm {
  studentId: string
  username: string
  email: string
  password: string
  confirmPassword: string
}
