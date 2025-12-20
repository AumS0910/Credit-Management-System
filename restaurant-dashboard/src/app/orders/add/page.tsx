"use client"

import React, { useState, useEffect } from "react"
import { motion } from "framer-motion"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"
import { RiShoppingBag3Line, RiUserLine, RiMoneyDollarCircleLine, RiAddLine, RiSubtractLine } from "react-icons/ri"
import { getApiUrl } from "@/lib/api"

interface Customer {
  id: string;
  name: string;
  creditBalance: number;
  active?: boolean;
}

// Update the MenuItem interface to include imageUrl
interface MenuItem {
  id: string;
  name: string;
  price: number;
  imageUrl: string;  // Add this line
  description: string; // Add this line if you want to show description
}

interface OrderItem {
  menuItemId: string;
  quantity: number;
  price: number;
  name: string;
}

export default function AddOrderPage() {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [customers, setCustomers] = useState<Customer[]>([])
  const [menuItems, setMenuItems] = useState<MenuItem[]>([])
  const [selectedItems, setSelectedItems] = useState<OrderItem[]>([])
  const [error, setError] = useState("")

  const [formData, setFormData] = useState({
    customerId: "",
    paymentMethod: "CASH",
    notes: "",
    tax: "0", // Changed to string
  })

  useEffect(() => {
    const selectedMenuItems = localStorage.getItem('selectedMenuItems')
    if (selectedMenuItems) {
      const items = JSON.parse(selectedMenuItems)
      setMenuItems(items)
      setSelectedItems(items.map(item => ({
        menuItemId: item.id,
        name: item.name,
        price: item.price,
        quantity: 1
      })))
      localStorage.removeItem('selectedMenuItems')
      fetchCustomers()
    } else {
      fetchCustomersAndMenuItems()
    }
  }, [])
    
  const fetchCustomers = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(getApiUrl('/customers'), {
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        setCustomers(data)
      } else {
        setError("Failed to fetch customers")
      }
    } catch (error) {
      console.error('Failed to fetch customers:', error)
      setError("Failed to load customers")
    }
  }

  const fetchMenuItems = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id, token } = JSON.parse(adminData)

      const response = await fetch(getApiUrl('/menu-items'), {
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString(),
          "Authorization": `Bearer ${token}`
        }
      })

      if (response.ok) {
        const data = await response.json()
        setMenuItems(data)
      } else {
        setError("Failed to fetch menu items")
      }
    } catch (error) {
      console.error('Failed to fetch menu items:', error)
      setError("Failed to load menu items")
    }
  }

  useEffect(() => {
    const init = async () => {
      await Promise.all([fetchCustomers(), fetchMenuItems()])
    }
    init()
  }, [])
  
  const fetchCustomersAndMenuItems = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) return
      const { id } = JSON.parse(adminData)

      const [customersRes, menuItemsRes] = await Promise.all([
        fetch(getApiUrl('/customers'), {
          headers: {
            "Content-Type": "application/json",
            "Admin-ID": id.toString()
          }
        }),
        fetch(getApiUrl('/menu-items'), {
          headers: {
            "Content-Type": "application/json",
            "Admin-ID": id.toString(),
            "Authorization": `Bearer ${localStorage.getItem('token')}`
          }
        })
      ])

      if (customersRes.ok && menuItemsRes.ok) {
        const customersData = await customersRes.json()
        let menuItemsData = await menuItemsRes.json()
        
        if (!Array.isArray(menuItemsData)) {
          menuItemsData = [menuItemsData]
        }

        menuItemsData = await Promise.all(menuItemsData.filter(item => item && item.name).map(async (item: MenuItem) => {
          try {
            const imageResponse = await fetch(getApiUrl(`/menu-items/pexels/food-image?name=${encodeURIComponent(item.name)}`))
            if (imageResponse.ok) {
              const imageData = await imageResponse.json()
              return { ...item, imageUrl: imageData.url }
            }
          } catch (error) {
            console.error('Failed to fetch image for', item.name, error)
          }
          return item
        }))

        setCustomers(customersData)
        setMenuItems(menuItemsData)
      }
    } catch (error) {
      console.error('Failed to fetch data:', error)
    }
  }

  useEffect(() => {
    fetchCustomersAndMenuItems()
  }, [])
  
  const addMenuItem = (menuItem: MenuItem) => {
    setSelectedItems(prev => {
      const existing = prev.find(item => item.menuItemId === menuItem.id)
      if (existing) {
        return prev.map(item =>
          item.menuItemId === menuItem.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        )
      }
      return [...prev, {
        menuItemId: menuItem.id,
        name: menuItem.name,
        price: menuItem.price,
        quantity: 1
      }]
    })
  }

  const updateQuantity = (menuItemId: string, change: number) => {
    setSelectedItems(prev => prev.map(item => {
      if (item.menuItemId === menuItemId) {
        const newQuantity = item.quantity + change
        return newQuantity > 0 ? { ...item, quantity: newQuantity } : item
      }
      return item
    }).filter(item => item.quantity > 0))
  }

  const calculateTotal = () => {
    const subtotal = selectedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    const tax = subtotal * (formData.tax / 100)
    return {
      subtotal,
      tax,
      total: subtotal + tax
    }
  }

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

      const { id } = JSON.parse(adminData)
      const { total, tax } = calculateTotal()

      // Remove the credit balance validation check
      // Only check if customer exists
      if (formData.paymentMethod === "CREDIT") {
        const customer = customers.find(c => c.id === formData.customerId)
        if (!customer) {
          setError("Customer not found")
          return
        }
      }

      const response = await fetch(getApiUrl("/orders"), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        },
        body: JSON.stringify({
          customerId: formData.customerId,
          menuItemIds: selectedItems.map(item => item.menuItemId),
          quantities: selectedItems.map(item => item.quantity),
          paymentMethod: formData.paymentMethod,
          notes: formData.notes,
          tax: tax,
          totalAmount: total
        })
      })

      if (response.ok) {
        router.push('/orders')
      } else {
        const data = await response.json()
        setError(data.message || "Failed to create order")
      }
    } catch (error) {
      setError("An error occurred. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  const { subtotal, tax, total } = calculateTotal()

  return (
    <div className="min-h-screen p-8 bg-background">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-6xl mx-auto space-y-8"
      >
        <div className="flex flex-col gap-2">
          <motion.h1
            className="text-3xl font-semibold tracking-tight"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            Create New Order
          </motion.h1>
          <motion.p
            className="text-muted-foreground"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            Add items and customer details to create a new order
          </motion.p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="grid gap-6 md:grid-cols-2">
            {/* Order Details Card */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.4 }}
            >
              <Card className="glass-card">
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <RiShoppingBag3Line className="h-6 w-6 text-primary" />
                    Order Details
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="customer">Customer</Label>
                    <div className="space-y-2">
                      <Select
                        id="customer"
                        value={formData.customerId}
                        onChange={(e) => setFormData({ ...formData, customerId: e.target.value })}
                        required
                      >
                        <option value="">Select a customer</option>
                        {customers && Array.isArray(customers) && customers.length > 0 ? (
                          customers
                            .filter(customer => customer && typeof customer === 'object' && customer.name && customer.id)
                            .map((customer) => (
                              <option key={customer.id} value={customer.id}>
                                {customer.name || 'Unknown Customer'} - Balance: ${customer.creditBalance ? customer.creditBalance.toFixed(2) : '0.00'}
                              </option>
                            ))
                        ) : (
                          <option disabled>Loading customers...</option>
                        )}
                      </Select>
                      {formData.customerId && customers.find(c => c.id === formData.customerId) && (
                        <div className="text-sm text-muted-foreground">
                          <p>Credit Balance: ${customers.find(c => c.id === formData.customerId)?.creditBalance.toFixed(2)}</p>
                          <p>Status: {customers.find(c => c.id === formData.customerId)?.active ? 'Active' : 'Inactive'}</p>
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="paymentMethod">Payment Method</Label>
                    <Select
                      id="paymentMethod"
                      value={formData.paymentMethod}
                      onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
                      required
                    >
                      <option value="CASH">Cash</option>
                      <option value="CREDIT">Credit</option>
                      <option value="CARD">Card</option>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="tax">Tax (%)</Label>
                    <Input
                      id="tax"
                      type="number"
                      min="0"
                      max="100"
                      step="0.1"
                      value={formData.tax}
                      onChange={(e) => {
                        const value = e.target.value;
                        setFormData({ ...formData, tax: value });
                      }}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="notes">Notes</Label>
                    <Textarea
                      id="notes"
                      placeholder="Add any special instructions..."
                      value={formData.notes}
                      onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                    />
                  </div>
                </CardContent>
              </Card>
            </motion.div>

            {/* Menu Items Section */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.5 }}
            >
              <Card className="glass-card">
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <RiShoppingBag3Line className="h-6 w-6 text-primary" />
                    Menu Items
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {menuItems && Array.isArray(menuItems) && menuItems.length > 0 ? (
                      menuItems
                        .filter(item => item && typeof item === 'object' && item.name && item.id)
                        .map((item) => (
                          <motion.div
                            key={item.id}
                            className="flex items-center gap-4 p-3 border rounded-lg hover:bg-gray-50"
                            whileHover={{ scale: 1.01 }}
                            whileTap={{ scale: 0.99 }}
                          >
                            <img
                              src={item.imageUrl || '/placeholder-food.jpg'}
                              alt={item.name || 'Menu Item'}
                              className="w-16 h-16 object-cover rounded-md"
                            />
                            <div className="flex-1">
                              <h3 className="font-medium">{item.name || 'Unknown Item'}</h3>
                              <p className="text-sm text-muted-foreground">
                                ${item.price ? item.price.toFixed(2) : '0.00'}
                              </p>
                            </div>
                            <div className="flex items-center gap-2">
                              {selectedItems.some(selected => selected && selected.menuItemId === item.id) ? (
                                <div className="flex items-center gap-2">
                                  <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => updateQuantity(item.id, -1)}
                                  >
                                    -
                                  </Button>
                                  <span className="w-8 text-center">
                                    {selectedItems.find(selected => selected && selected.menuItemId === item.id)?.quantity || 0}
                                  </span>
                                  <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => updateQuantity(item.id, 1)}
                                  >
                                    +
                                  </Button>
                                </div>
                              ) : (
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => addMenuItem(item)}
                                >
                                  Add
                                </Button>
                              )}
                            </div>
                          </motion.div>
                        ))
                    ) : (
                      <div className="text-center py-8 text-muted-foreground">
                        {loading ? 'Loading menu items...' : 'No menu items available'}
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          </div>

          {/* Remove the entire Selected Items Card section and replace with just the totals */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.6 }}
          >
            <Card className="glass-card">
              <CardHeader>
                <CardTitle>Order Summary</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span>Subtotal</span>
                    <span>${subtotal.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Tax ({formData.tax}%)</span>
                    <span>${tax.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between font-semibold">
                    <span>Total</span>
                    <span>${total.toFixed(2)}</span>
                  </div>
                </div>
              </CardContent>
            </Card>
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
            transition={{ duration: 0.5, delay: 0.7 }}
          >
            <Button
              type="button"
              variant="outline"
              className="flex-1"
              onClick={() => router.back()}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              className="flex-1"
              disabled={loading || selectedItems.length === 0 || !formData.customerId}
            >
              {loading ? "Creating..." : "Create Order"}
            </Button>
          </motion.div>
        </form>
      </motion.div>
    </div>
  )
}