"use client"

import React, { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Sidebar } from "@/components/sidebar"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select } from "@/components/ui/select"
import { Label } from "@/components/ui/label"
import { motion } from "framer-motion"

interface OrderData {
  id: number
  customerId: number
  status: string
  paymentMethod: string
  totalAmount: number
  notes: string
}

export default function EditOrderPage({ params }: { params: Promise<{ id: string }> }) {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [orderData, setOrderData] = useState<OrderData | null>(null)
  const resolvedParams = React.use(params)
  const orderId = resolvedParams.id

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        const adminData = localStorage.getItem('adminData')
        if (!adminData) {
          router.push('/login')
          return
        }
        const { id } = JSON.parse(adminData)

        const response = await fetch(`http://localhost:8080/api/orders/${orderId}`, {
          headers: {
            "Content-Type": "application/json",
            "Admin-ID": id.toString()
          }
        })

        if (response.ok) {
          const data = await response.json()
          setOrderData(data)
        } else {
          setError("Failed to fetch order")
        }
      } catch (error) {
        setError("An error occurred while fetching the order")
      }
    }

    fetchOrder()
  }, [orderId])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!orderData) return

    setLoading(true)
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(`http://localhost:8080/api/orders/${orderId}`, {
        method: 'PUT',
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        },
        body: JSON.stringify(orderData)
      })

      if (response.ok) {
        router.push('/orders')
      } else {
        const data = await response.json()
        setError(data.message || "Failed to update order")
      }
    } catch (error) {
      setError("An error occurred while updating the order")
    } finally {
      setLoading(false)
    }
  }

  if (!orderData) return <div>Loading...</div>

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1">
        <div className="min-h-screen p-8 bg-background">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <Card>
              <CardHeader>
                <CardTitle>Edit Order #{orderId}</CardTitle>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <Label htmlFor="status">Status</Label>
                    <Select
                      id="status"
                      value={orderData.status}
                      onChange={(e) => setOrderData({ ...orderData, status: e.target.value })}
                    >
                      <option value="PENDING">Pending</option>
                      <option value="COMPLETED">Completed</option>
                      <option value="CANCELLED">Cancelled</option>
                    </Select>
                  </div>

                  <div>
                    <Label htmlFor="paymentMethod">Payment Method</Label>
                    <Select
                      id="paymentMethod"
                      value={orderData.paymentMethod}
                      onChange={(e) => setOrderData({ ...orderData, paymentMethod: e.target.value })}
                    >
                      <option value="CASH">Cash</option>
                      <option value="CREDIT">Credit</option>
                      <option value="CARD">Card</option>
                    </Select>
                  </div>

                  <div>
                    <Label htmlFor="notes">Notes</Label>
                    <Input
                      id="notes"
                      value={orderData.notes || ''}
                      onChange={(e) => setOrderData({ ...orderData, notes: e.target.value })}
                    />
                  </div>

                  {error && <div className="text-red-500">{error}</div>}

                  <div className="flex gap-2">
                    <Button type="submit" disabled={loading}>
                      {loading ? "Updating..." : "Update Order"}
                    </Button>
                    <Button 
                      type="button" 
                      variant="outline"
                      onClick={() => router.push('/orders')}
                    >
                      Cancel
                    </Button>
                  </div>
                </form>
              </CardContent>
            </Card>
          </motion.div>
        </div>
      </main>
    </div>
  )
}