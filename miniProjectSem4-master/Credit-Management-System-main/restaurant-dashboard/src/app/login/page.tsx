"use client"

import React, { useState } from 'react'
import { FaFacebookF, FaGooglePlusG, FaLinkedinIn, FaUser, FaLock, FaEnvelope } from 'react-icons/fa'
import { useRouter } from 'next/navigation'
import styles from './login.module.css'

const LoginPage: React.FC = () => {
  const router = useRouter()
  const [isRightPanelActive, setIsRightPanelActive] = useState(false)
  const [showForgot, setShowForgot] = useState(false)
  const [isLeftPanelActive, setIsLeftPanelActive] = useState(false)

  // Form states
  const [registerData, setRegisterData] = useState({
    name: '',
    username: '',
    password: ''
  })

  const [loginData, setLoginData] = useState({
    username: '',
    password: ''
  })

  const [error, setError] = useState('')

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const response = await fetch('http://localhost:8080/api/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(registerData),
      })

      const data = await response.json()

      if (response.ok) {
        setIsRightPanelActive(false) // Switch to login panel
        setError('')
      } else {
        setError(data.error || 'Registration failed')
      }
    } catch (err) {
      setError('Registration failed. Please try again.')
    }
  }

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const response = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginData),
      })

      const data = await response.json()

      if (response.ok) {
        // Store token and admin data
        localStorage.setItem('token', data.token)
        localStorage.setItem('adminData', JSON.stringify({
          id: data.id,
          username: data.username
        }))
        router.push('/dashboard') // Redirect to dashboard
      } else {
        setError(data.error || 'Login failed')
      }
    } catch (err) {
      setError('Login failed. Please try again.')
    }
  }

  return (
    <main className={styles.loginBody}>
      <div className={`${styles.container} ${isRightPanelActive ? styles.rightPanelActive : ''} ${isLeftPanelActive ? styles.leftPanelActive : ''}`}>
        <div className={`${styles.formContainer} ${styles.signUpContainer}`}>
          <form className={styles.loginForm} onSubmit={handleRegister}>
            <h1 className={styles.heading1}>Create Account</h1>
            {error && <p className="text-red-500 text-sm">{error}</p>}
            <div className={styles.socialContainer}>
              <a href="#" className={styles.link}><FaFacebookF /></a>
              <a href="#" className={styles.link}><FaGooglePlusG /></a>
              <a href="#" className={styles.link}><FaLinkedinIn /></a>
            </div>
            <span className={styles.textSpan}>or use your email for registration</span>
            <div className={styles.accountInput}>
              <FaUser className="text-gray-500" />
              <input 
                className={styles.input}
                type="text" 
                placeholder="Name" 
                value={registerData.name}
                onChange={(e) => setRegisterData({...registerData, name: e.target.value})}
              />
            </div>
            <div className="account-input">
              <FaEnvelope className="text-gray-500" />
              <input 
                type="text" 
                placeholder="Username" 
                value={registerData.username}
                onChange={(e) => setRegisterData({...registerData, username: e.target.value})}
              />
            </div>
            <div className="account-input">
              <FaLock className="text-gray-500" />
              <input 
                type="password" 
                placeholder="Password" 
                value={registerData.password}
                onChange={(e) => setRegisterData({...registerData, password: e.target.value})}
              />
            </div>
            <button className={styles.button} type="submit">Sign Up</button>
          </form>
        </div>

        <div className={`${styles.formContainer} ${styles.signInContainer}`}>
          <form className={styles.loginForm} onSubmit={handleLogin}>
            <div 
              className={showForgot ? styles.show : styles.hide} 
              id={styles.forgot}
              style={{ 
                transition: 'all 0.5s ease-in-out',
                transform: showForgot ? 'translateY(0)' : 'translateY(-100%)',
                opacity: showForgot ? 1 : 0 
              }}
            >
              <div className={styles.enterEmail}>
                <div className={styles.enterEmailDetail}>
                  <h1 className={styles.heading1}>Do you forgot?</h1>
                  <p className={styles.paragraph}>Just enter your email to retrieve your password</p>
                  <div className={styles.accountInput}>
                    <FaEnvelope className="text-gray-500" />
                    <input className={styles.input} type="email" placeholder="Email" />
                  </div>
                  <div>
                    <button className={styles.button}>Send</button>
                    <p onClick={() => slideAnimation(false)} className="cursor-pointer">
                      <u>close</u>
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <h1>Sign in</h1>
            {error && <p className="text-red-500 text-sm">{error}</p>}
            <div className="social-container">
              <a href="#" className="social"><FaFacebookF /></a>
              <a href="#" className="social"><FaGooglePlusG /></a>
              <a href="#" className="social"><FaLinkedinIn /></a>
            </div>
            <span>or use your account</span>
            <div className="account-input">
              <FaEnvelope className="text-gray-500" />
              <input 
                type="text" 
                placeholder="Username" 
                value={loginData.username}
                onChange={(e) => setLoginData({...loginData, username: e.target.value})}
              />
            </div>
            <div className="account-input">
              <FaLock className="text-gray-500" />
              <input 
                type="password" 
                placeholder="Password" 
                value={loginData.password}
                onChange={(e) => setLoginData({...loginData, password: e.target.value})}
              />
            </div>
            <p 
              className={styles.forgotButton}
              onClick={() => {
                slideAnimation(true)
                setIsLeftPanelActive(true)
              }}
            >
              Forgot your password?
            </p>
            <button className={styles.button}>Sign In</button>
          </form>
        </div>

        <div className={styles.overlayContainer}>
          <div className={styles.overlay}>
            <div className={styles.square}></div>
            <div className={styles.triangle}></div>
            <div className={styles.circle}></div>
            <div className={styles.square2}></div>
            <div className={styles.triangle2}></div>
            
            <div className={`${styles.overlayPanel} ${styles.overlayLeft}`}>
              <h1 className={styles.heading1}>Welcome Back!</h1>
              <p className={styles.paragraph}>To keep connected with us please login with your personal info</p>
              <button 
                className={`${styles.button} ${styles.ghostButton}`}
                onClick={() => {
                  setIsRightPanelActive(false)
                  setIsLeftPanelActive(false)
                }}
              >
                Sign In
              </button>
            </div>

            <div className={`${styles.overlayPanel} ${styles.overlayRight}`}>
              <h1 className={styles.heading1}>Hello, Friend!</h1>
              <p className={styles.paragraph}>Enter your personal details and start journey with us</p>
              <button 
                className={`${styles.button} ${styles.ghostButton}`}
                onClick={() => setIsRightPanelActive(true)}
              >
                Sign Up
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
};

export default LoginPage;