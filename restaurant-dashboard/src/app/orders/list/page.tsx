"use client"

import React, { useState, useEffect, useRef } from "react"
import { useRouter } from "next/navigation"
import { Sidebar } from "@/components/sidebar"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { RiShoppingBag3Line, RiAddLine, RiEyeLine, RiEditLine, RiDeleteBinLine, RiPlayLine, RiCheckLine, RiCloseLine } from "react-icons/ri"
import { motion } from "framer-motion" // Add this import
import { getApiUrl } from "@/lib/api"
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
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
  const stompClientRef = useRef<any>(null)

  useEffect(() => {
    fetchOrders()
    connectWebSocket()

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate()
      }
    }
  }, [])

  const connectWebSocket = () => {
    // Use the API base URL for WebSocket connection
    const apiBaseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'

    // For production (HTTPS), use secure WebSocket
    // For development (HTTP), use regular SockJS
    const isProduction = typeof window !== 'undefined' && window.location.protocol === 'https:'

    let socket
    if (isProduction) {
      // In production, try direct WebSocket first (better for HTTPS)
      try {
        const wsUrl = apiBaseUrl.replace(/^https:/, 'wss:') + '/ws'
        console.log('Connecting to secure WebSocket:', wsUrl)
        socket = new WebSocket(wsUrl)
        stompClientRef.current = Stomp.over(socket)
      } catch (error) {
        console.log('Direct WebSocket failed in production, this should not happen')
        return
      }
    } else {
      // In development, use SockJS (works with HTTP)
      const wsUrl = `${apiBaseUrl}/ws`
      console.log('Connecting to SockJS:', wsUrl)
      socket = new SockJS(wsUrl)
      stompClientRef.current = Stomp.over(socket)
    }

    stompClientRef.current.connect({}, (frame: any) => {
      console.log('Connected to WebSocket:', frame)

      // Subscribe to order updates
      stompClientRef.current.subscribe('/topic/orders', (message: any) => {
        const orderUpdate = JSON.parse(message.body)
        handleOrderUpdate(orderUpdate)
      })

      // Subscribe to notifications
      stompClientRef.current.subscribe('/topic/notifications', (message: any) => {
        const notification = JSON.parse(message.body)
        handleNotification(notification)
      })

      // Send initial connection message
      stompClientRef.current.send('/app/connect', {}, JSON.stringify({ type: 'ADMIN_CONNECTED' }))

    }, (error: any) => {
      console.error('WebSocket connection error:', error)
      // Retry connection after 5 seconds
      setTimeout(connectWebSocket, 5000)
    })
  }

  const handleOrderUpdate = (orderUpdate: any) => {
    console.log('Real-time order update received:', orderUpdate)

    // Refresh the orders list when an update is received
    fetchOrders()

    // Show appropriate notification based on action
    const actionMessages = {
      'CREATED': 'New order has been created',
      'STATUS_CHANGED': 'Order status has been updated',
      'UPDATED': 'Order has been updated',
      'CANCELLED': 'Order has been cancelled'
    }

    toast.success(`Order Update`, {
      description: actionMessages[orderUpdate.action as keyof typeof actionMessages] || 'Order has been updated',
      duration: 4000,
    })
  }

  const handleNotification = (notification: any) => {
    console.log('Real-time notification received:', notification)

    if (notification.type === 'success') {
      toast.success(notification.message, { duration: 5000 })
    } else if (notification.type === 'error') {
      toast.error(notification.message, { duration: 5000 })
    } else if (notification.type === 'info') {
      toast.info(notification.message, { duration: 5000 })
    } else {
      toast(notification.message, { duration: 5000 })
    }
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

  const handleStartOrder = async (orderId: string) => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl(`/orders/${orderId}/start`), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        // Real-time polling will detect the change and show notification
        console.log("Order started successfully")
      } else {
        const errorData = await response.json()
        setError(errorData.message || "Failed to start order")
      }
    } catch (error) {
      setError("An error occurred while starting the order")
    }
  }

  const handleCompleteOrder = async (orderId: string) => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl(`/orders/${orderId}/complete`), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        console.log("Order completed successfully")
      } else {
        const errorData = await response.json()
        setError(errorData.message || "Failed to complete order")
      }
    } catch (error) {
      setError("An error occurred while completing the order")
    }
  }

  const handleCancelOrder = async (orderId: string) => {
    if (!confirm("Are you sure you want to cancel this order?")) return

    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl(`/orders/${orderId}/cancel`), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        console.log("Order cancelled successfully")
      } else {
        const errorData = await response.json()
        setError(errorData.message || "Failed to cancel order")
      }
    } catch (error) {
      setError("An error occurred while cancelling the order")
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
                    <CardTitle className="flex items-center gap-2">
                      <RiShoppingBag3Line className="h-6 w-6 text-primary" />
                      Recent Orders
                      <div className="ml-auto text-xs text-muted-foreground">
                        Live Updates Active
                      </div>
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

                              {/* Action buttons based on order status */}
                              {order.status === 'PENDING' && (
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => handleStartOrder(order.id.toString())}
                                  className="text-green-600 hover:text-green-700"
                                  title="Start Order"
                                >
                                  <RiPlayLine className="h-4 w-4" />
                                </Button>
                              )}

                              {order.status === 'APPROVED' && (
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => handleCompleteOrder(order.id.toString())}
                                  className="text-blue-600 hover:text-blue-700"
                                  title="Complete Order"
                                >
                                  <RiCheckLine className="h-4 w-4" />
                                </Button>
                              )}

                              {order.status !== 'COMPLETED' && order.status !== 'CANCELLED' && (
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => handleCancelOrder(order.id.toString())}
                                  className="text-orange-600 hover:text-orange-700"
                                  title="Cancel Order"
                                >
                                  <RiCloseLine className="h-4 w-4" />
                                </Button>
                              )}

                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEdit(order.id)}
                                title="Edit Order"
                              >
                                <RiEditLine className="h-4 w-4" />
                              </Button>

                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleDelete(order.id)}
                                className="text-red-500 hover:text-red-700"
                                title="Delete Order"
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