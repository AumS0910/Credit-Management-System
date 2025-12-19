const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'https://credit-management-system-40i5.onrender.com'

export const API_URL = `${API_BASE_URL}/api`

export const getApiUrl = (endpoint: string) => `${API_BASE_URL}${endpoint}`