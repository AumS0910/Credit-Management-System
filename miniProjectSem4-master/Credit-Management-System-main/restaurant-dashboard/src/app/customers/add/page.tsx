"use client"

import React, { useState } from "react"
import { motion } from "framer-motion"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { RiUserAddLine, RiMailLine, RiPhoneLine, RiMapPinLine, RiMoneyDollarCircleLine } from "react-icons/ri"

export default function AddCustomerPage() {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    address: "",
    totalCredit: ""
  })
  const [error, setError] = useState("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id: adminId } = JSON.parse(adminData)
      const response = await fetch("http://localhost:8080/customers/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": adminId.toString()
        },
        body: JSON.stringify({
          ...formData,
          totalCredit: parseFloat(formData.totalCredit),
          creditBalance: 0,
          active: true,
          adminId: adminId
        })
      })

      if (response.ok) {
        router.push('/customers/list')
      } else {
        const data = await response.json()
        setError(data.message || "Failed to add customer")
      }
    } catch (error) {
      console.error('Failed to create customer:', error)
      setError("An error occurred. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen p-8 bg-background">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-2xl mx-auto space-y-6"
      >
        <div className="flex flex-col gap-2">
          <motion.h1
            className="text-3xl font-semibold tracking-tight"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            Add New Customer
          </motion.h1>
          <motion.p
            className="text-muted-foreground"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            Enter customer details to create a new account
          </motion.p>
        </div>

        <Card className="glass-card border-0 shadow-2xl">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <RiUserAddLine className="h-6 w-6 text-primary" />
              Customer Information
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <motion.div
                className="space-y-4"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.4 }}
              >
                <div className="space-y-2">
                  <Label htmlFor="name">Full Name</Label>
                  <div className="relative">
                    <RiUserAddLine className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                    <Input
                      id="name"
                      placeholder="Enter customer name"
                      className="pl-10"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <div className="relative">
                      <RiMailLine className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                      <Input
                        id="email"
                        type="email"
                        placeholder="customer@example.com"
                        className="pl-10"
                        value={formData.email}
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        required
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="phone">Phone Number</Label>
                    <div className="relative">
                      <RiPhoneLine className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                      <Input
                        id="phone"
                        placeholder="Enter phone number"
                        className="pl-10"
                        value={formData.phone}
                        onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                        required
                      />
                    </div>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="address">Address</Label>
                  <div className="relative">
                    <RiMapPinLine className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                    <Input
                      id="address"
                      placeholder="Enter customer address"
                      className="pl-10"
                      value={formData.address}
                      onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                      required
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="totalCredit">Credit Limit</Label>
                  <div className="relative">
                    <RiMoneyDollarCircleLine className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                    <Input
                      id="totalCredit"
                      type="number"
                      placeholder="Enter credit limit"
                      className="pl-10"
                      value={formData.totalCredit}
                      onChange={(e) => setFormData({ ...formData, totalCredit: e.target.value })}
                      required
                      min="0"
                      step="0.01"
                    />
                  </div>
                </div>
              </motion.div>

              {error && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="text-red-500 text-sm text-center"
                >
                  {error}
                </motion.div>
              )}

              <motion.div
                className="flex gap-4"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.6 }}
              >
                <button
                  type="button"
                  onClick={() => router.back()}
                  className="flex-1 px-4 py-2 border border-input rounded-md hover:bg-accent transition-colors"
                >
                  Cancel
                </button>
                <motion.button
                  type="submit"
                  className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-md font-medium hover:bg-primary/90 transition-colors disabled:opacity-50"
                  disabled={loading}
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  {loading ? "Adding..." : "Add Customer"}
                </motion.button>
              </motion.div>
            </form>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  )
}