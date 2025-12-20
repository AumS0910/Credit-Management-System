const API_BASE_URL = (process.env.NEXT_PUBLIC_API_BASE_URL || 'https://credit-management-system-40i5.onrender.com').replace(/\/$/, '')

console.log('🔧 API Configuration:', {
  NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL,
  API_BASE_URL,
  API_URL: `${API_BASE_URL}/api`
})

export const API_URL = `${API_BASE_URL}/api`

export const getApiUrl = (endpoint: string) => {
  // Ensure endpoint starts with /
  const normalizedEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`
  const url = `${API_BASE_URL}/api${normalizedEndpoint}`
  console.log('🔗 API Call URL:', url)
  return url
}