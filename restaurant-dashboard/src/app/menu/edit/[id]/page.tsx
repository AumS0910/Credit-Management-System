"use client"

import React, { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { motion } from "framer-motion"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Select } from "@/components/ui/select"
import { Sidebar } from "@/components/sidebar"

interface MenuItem {
  id: number
  name: string
  description: string
  price: number
  category: string
  imageUrl?: string
}

export default function EditMenuItemPage({ params }: { params: { id: string } }) {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [menuItem, setMenuItem] = useState<MenuItem | null>(null)
  const id = React.use(params).id  // Fix: Unwrap params with React.use()

  useEffect(() => {
    const fetchMenuItem = async () => {
      try {
        const adminData = localStorage.getItem('adminData')
        if (!adminData) {
          router.push('/login')
          return
        }
        const { id: adminId } = JSON.parse(adminData)

        // Updated endpoint to match backend structure
        const response = await fetch(`http://localhost:8080/api/menu-items/get/${id}`, {
          headers: {
            "Content-Type": "application/json",
            "Admin-ID": adminId.toString(),
            "Authorization": `Bearer ${localStorage.getItem('token')}`
          }
        })

        if (response.ok) {
          const data = await response.json()
          setMenuItem(data)
        } else {
          setError("Failed to fetch menu item")
        }
      } catch (error) {
        setError("An error occurred while fetching the menu item")
      }
    }

    fetchMenuItem()
  }, [id]) // Updated dependency array to use unwrapped id

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!menuItem) return

    setLoading(true)
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id: adminId } = JSON.parse(adminData)

      const response = await fetch(`http://localhost:8080/api/menu-items/${id}`, {
        method: 'PUT',
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": adminId.toString(),
          "Authorization": `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          ...menuItem,
          adminId: adminId
        })
      })

      if (response.ok) {
        router.push('/menu/list')  // Changed from '/menu/page' to '/menu/list'
      } else {
        const data = await response.json()
        setError(data.message || "Failed to update menu item")
      }
    } catch (error) {
      setError("An error occurred while updating the menu item")
    } finally {
      setLoading(false)
    }
  }

  if (!menuItem) return <div>Loading...</div>

  return (
    <div className="flex">
      <Sidebar />
      <div className="flex-1 min-h-screen p-8 bg-background">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Card>
            <CardHeader>
              <CardTitle>Edit Menu Item</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <Label htmlFor="name">Name</Label>
                  <Input
                    id="name"
                    value={menuItem.name}
                    onChange={(e) => setMenuItem({ ...menuItem, name: e.target.value })}
                  />
                </div>

                <div>
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    value={menuItem.description}
                    onChange={(e) => setMenuItem({ ...menuItem, description: e.target.value })}
                  />
                </div>

                <div>
                  <Label htmlFor="price">Price</Label>
                  <Input
                    id="price"
                    type="number"
                    step="0.01"
                    value={menuItem.price}
                    onChange={(e) => setMenuItem({ ...menuItem, price: parseFloat(e.target.value) })}
                  />
                </div>

                <div>
                  <Label htmlFor="category">Category</Label>
                  <Select
                    id="category"
                    value={menuItem.category}
                    onChange={(e) => setMenuItem({ ...menuItem, category: e.target.value })}
                  >
                    <option value="APPETIZER">Appetizer</option>
                    <option value="MAIN_COURSE">Main Course</option>
                    <option value="DESSERT">Dessert</option>
                    <option value="BEVERAGE">Beverage</option>
                  </Select>
                </div>

                {error && <div className="text-red-500">{error}</div>}

                <div className="flex gap-2">
                  <Button type="submit" disabled={loading}>
                    {loading ? "Updating..." : "Update Menu Item"}
                  </Button>
                  <Button 
                    type="button" 
                    variant="outline"
                    onClick={() => router.push('/menu/list')}
                  >
                    Cancel
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}