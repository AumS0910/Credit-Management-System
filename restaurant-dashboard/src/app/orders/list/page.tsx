"use client"

import React, { useState, useEffect, useRef } from "react"
import { useRouter } from "next/navigation"
import { Sidebar } from "@/components/sidebar"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { RiShoppingBag3Line, RiAddLine, RiEyeLine, RiEditLine, RiDeleteBinLine } from "react-icons/ri"
import { motion } from "framer-motion" // Add this import
import { getApiUrl } from "@/lib/api"
import { toast } from "sonner"

interface Order {
  id: string;
  customer: {
    name: string;
  };
  orderDate: string;
  totalAmount: number;
  status: string;
  paymentMethod: string;
  notes?: string;
}

export default function OrderListPage() {
  const router = useRouter()
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null)
  const previousOrdersRef = useRef<Order[]>([])

  useEffect(() => {
    fetchOrders()
    const cleanup = startRealTimeUpdates()
    return cleanup
  }, [])

  // Real-time polling for order updates (more reliable than WebSocket for HTTPS)
  const startRealTimeUpdates = () => {
    // Poll for updates every 10 seconds
    const interval = setInterval(async () => {
      try {
        const adminData = localStorage.getItem('adminData')
        if (!adminData) return

        const { id } = JSON.parse(adminData)
        const response = await fetch(getApiUrl(`/orders`), {
          headers: {
            "Content-Type": "application/json",
            "Admin-ID": id.toString()
          }
        })

        if (response.ok) {
          const newOrders = await response.json()

          // Check for changes and show notifications
          if (previousOrdersRef.current.length > 0) {
            detectOrderChanges(previousOrdersRef.current, newOrders)
          }

          previousOrdersRef.current = [...newOrders]
          setOrders(newOrders)
          setLastUpdate(new Date())
        }
      } catch (error) {
        console.error('Real-time update error:', error)
      }
    }, 10000) // 10 second intervals

    return () => clearInterval(interval)
  }

  const detectOrderChanges = (oldOrders: Order[], newOrders: Order[]) => {
    const oldOrderMap = new Map(oldOrders.map(order => [order.id, order]))
    const newOrderMap = new Map(newOrders.map(order => [order.id, order]))

    // Check for status changes
    newOrders.forEach(newOrder => {
      const oldOrder = oldOrderMap.get(newOrder.id)
      if (oldOrder && oldOrder.status !== newOrder.status) {
        const statusMessages = {
          'PENDING': 'Order is now pending',
          'APPROVED': 'Order has been approved and is being prepared',
          'COMPLETED': 'Order has been completed',
          'CANCELLED': 'Order has been cancelled'
        }

        toast.success(`Order #${newOrder.id} Status Update`, {
          description: statusMessages[newOrder.status as keyof typeof statusMessages] || 'Status changed to ' + newOrder.status,
          duration: 5000,
        })
      }
    })

    // Check for new orders
    newOrders.forEach(newOrder => {
      if (!oldOrderMap.has(newOrder.id)) {
        toast.info(`New Order Received`, {
          description: `Order #${newOrder.id} from ${newOrder.customer.name} - $${newOrder.totalAmount.toFixed(2)}`,
          duration: 5000,
        })
      }
    })
  }

  const fetchOrders = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl(`/orders`), {
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        setOrders(data)
        setLastUpdate(new Date())
        setError("")
      } else {
        setError("Failed to fetch orders")
      }
    } catch (error) {
      setError("An error occurred while fetching orders")
    } finally {
      setLoading(false)
    }
  }

  const handleEdit = async (orderId: string) => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      // First fetch the order details
      const response = await fetch(getApiUrl(`/orders/${orderId}`), {
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        const orderData = await response.json()
        // Store order data in localStorage
        localStorage.setItem('editOrder', JSON.stringify(orderData))
        // Navigate to edit page
        window.location.href = `/orders/edit/${orderId}`
      } else {
        setError("Failed to fetch order details")
      }
    } catch (error) {
      console.error("Navigation error:", error)
      setError("Failed to navigate to edit page")
    }
  }

  const handleDelete = async (orderId: string) => {
    if (!confirm("Are you sure you want to delete this order?")) return

    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl(`/orders/${orderId}`), {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        setOrders(orders.filter(order => order.id !== orderId))
      } else {
        const errorData = await response.json()
        setError(errorData.message || "Failed to delete order")
      }
    } catch (error) {
      setError("An error occurred while deleting the order")
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    })
  }

  return (
    <div className="flex">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="flex justify-between items-center mb-6">
          <div className="flex-1 min-h-screen p-8 bg-background">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
              className="max-w-6xl mx-auto space-y-8"
            >
              <div className="flex justify-between items-center">
                <div>
                  <motion.h1
                    className="text-3xl font-semibold tracking-tight"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.5, delay: 0.2 }}
                  >
                    Orders
                  </motion.h1>
                  <motion.p
                    className="text-muted-foreground"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.5, delay: 0.3 }}
                  >
                    Manage your restaurant orders
                  </motion.p>
                </div>
                <motion.div
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.5, delay: 0.4 }}
                >
                  <Button
                    onClick={() => router.push('/orders/add')}
                    className="flex items-center gap-2"
                  >
                    <RiAddLine className="h-5 w-5" />
                    New Order
                  </Button>
                </motion.div>
              </div>

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
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.5 }}
              >
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <RiShoppingBag3Line className="h-6 w-6 text-primary" />
                        Recent Orders
                      </div>
                      {lastUpdate && (
                        <div className="text-xs text-muted-foreground">
                          Last updated: {lastUpdate.toLocaleTimeString()}
                        </div>
                      )}
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      {loading ? (
                        <div className="text-center py-4">Loading orders...</div>
                      ) : orders.length === 0 ? (
                        <div className="text-center py-4 text-muted-foreground">
                          No orders found
                        </div>
                      ) : (
                        orders.map((order) => (
                          <motion.div
                            key={order.id}
                            className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50"
                            whileHover={{ scale: 1.01 }}
                            whileTap={{ scale: 0.99 }}
                          >
                            <div className="flex-1">
                              <h3 className="font-medium">Order #{order.id}</h3>
                              <p className="text-sm text-muted-foreground">
                                {order.customer.name} • {formatDate(order.orderDate)}
                              </p>
                              <div className="flex gap-2 mt-1">
                                <span className={`text-xs px-2 py-1 rounded-full ${
                                  order.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                                  order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                                  'bg-gray-100 text-gray-800'
                                }`}>
                                  {order.status}
                                </span>
                                <span className="text-xs px-2 py-1 rounded-full bg-blue-100 text-blue-800">
                                  {order.paymentMethod}
                                </span>
                              </div>
                            </div>
                            <div className="flex items-center gap-2">
                              <p className="font-medium">${order.totalAmount.toFixed(2)}</p>
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEdit(order.id)}
                              >
                                <RiEditLine className="h-4 w-4" />
                              </Button>
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleDelete(order.id)}
                                className="text-red-500 hover:text-red-700"
                              >
                                <RiDeleteBinLine className="h-4 w-4" />
                              </Button>
                            </div>
                          </motion.div>
                        ))
                      )}
                    </div>
                  </CardContent>
                </Card>
              </motion.div>
            </motion.div>
          </div>
        </div>
      </main>
    </div>
  )
}