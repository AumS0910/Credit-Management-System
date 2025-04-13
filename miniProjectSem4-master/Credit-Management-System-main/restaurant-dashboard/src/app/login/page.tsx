"use client"

import React, { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { useRouter } from "next/navigation"
import { RiRestaurantLine } from "react-icons/ri"

const images = [
  "/images/restaurant 1.jpg",  // Add your restaurant images
  "/images/restaurant 2.jpg",  // in the public folder
  "/images/restaurant 3.jpg",
]

export default function LoginPage() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [currentImage, setCurrentImage] = useState(0)
  const router = useRouter()

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentImage((prev) => (prev >= 2 ? 0 : prev + 1))
    }, 3000)

    return () => clearInterval(timer)
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const response = await fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      })

      const data = await response.json()

      if (response.ok) {
        localStorage.setItem('adminData', JSON.stringify({
          id: data.id,
          username: data.username,
          name: data.name
        }))
        
        if (data.token) {
          localStorage.setItem('token', data.token)
        }
        
        router.push('/dashboard')
      } else {
        setError(data.error || "Invalid credentials")
      }
    } catch (error) {
      setError("An error occurred. Please try again.")
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <div className="flex w-full max-w-4xl h-[600px]"> {/* Increased height */}
        {/* Image Slider Section */}
        <div className="hidden lg:block w-1/2 relative overflow-hidden rounded-l-lg">
          <AnimatePresence initial={false}>
            <motion.img
              key={currentImage}
              src={images[currentImage]}
              alt="Restaurant"
              className="h-full w-full object-cover"
              initial={{ opacity: 0, x: 100 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -100 }}
              transition={{ duration: 0.5 }}
            />
          </AnimatePresence>
          <div className="absolute inset-0 bg-black/40 flex items-end justify-center pb-8"> {/* Changed to items-end and added padding bottom */}
            <div className="text-white text-center">
              <h1 className="text-2xl font-bold mb-2">Welcome to Our Restaurant</h1>
              <p className="text-sm">Manage your restaurant with ease</p>
            </div>
          </div>
        </div>

        {/* Login Form Section */}
        <motion.div 
          className="w-full lg:w-1/2 h-full" 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Card className="glass-card border-0 shadow-2xl rounded-lg lg:rounded-l-none h-full"> {/* Added h-full */}
            <CardHeader className="space-y-1 text-center">
              <div className="flex justify-center mb-4">
                <RiRestaurantLine className="h-12 w-12 text-primary" />
              </div>
              <CardTitle className="text-3xl font-bold">Welcome Back</CardTitle>
              <p className="text-muted-foreground">Enter your credentials to continue</p>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                    Username
                  </label>
                  <input
                    type="text"
                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                    placeholder="Enter your username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                    Password
                  </label>
                  <input
                    type="password"
                    className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </div>
                {error && (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="text-red-500 text-sm text-center"
                  >
                    {error}
                  </motion.div>
                )}
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  className="w-full h-10 px-4 py-2 bg-primary text-primary-foreground rounded-md font-medium hover:bg-primary/90 transition-colors"
                  type="submit"
                >
                  Sign In
                </motion.button>
                <p className="text-center text-sm text-muted-foreground">
                  Don't have an account?{" "}
                  <a href="/register" className="text-primary hover:underline">
                    Register
                  </a>
                </p>
              </form>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}