"use client"

import React, { useEffect, useState } from "react"
import { motion } from "framer-motion"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { RiRestaurantLine, RiSearchLine, RiAddLine, RiEditLine, RiDeleteBinLine } from "react-icons/ri"
import { RiShoppingCartLine } from "react-icons/ri"

interface MenuItem {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  prepTime: number;
  isSpecial: boolean;
  available: boolean;
  rating: number;
}

export default function MenuListPage() {
  const router = useRouter()
  const [menuItems, setMenuItems] = useState<MenuItem[]>([])
  const [loading, setLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    fetchMenuItems()
  }, [])

  const fetchMenuItems = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id } = JSON.parse(adminData)
      const response = await fetch(`http://localhost:8080/menu-items/list`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Admin-ID': id.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        // For each menu item, fetch its image from Pexels
        const itemsWithImages = await Promise.all(data.map(async (item) => {
          const imageResponse = await fetch(`http://localhost:8080/api/pexels/food-image?name=${encodeURIComponent(item.name)}`)
          if (imageResponse.ok) {
            const imageData = await imageResponse.json()
            return { ...item, imageUrl: imageData.url }
          }
          return item
        }))
        setMenuItems(itemsWithImages)
      }
    } catch (error) {
      console.error('Failed to fetch menu items:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      fetchMenuItems()
      return
    }

    try {
      const response = await fetch(`http://localhost:8080/api/menu-items/search?query=${searchQuery}`)
      if (response.ok) {
        const data = await response.json()
        setMenuItems(data)
      }
    } catch (error) {
      console.error('Failed to search menu items:', error)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this menu item?')) return

    try {
      const response = await fetch(`http://localhost:8080/api/menu-items/${id}`, {
        method: 'DELETE'
      })

      if (response.ok) {
        setMenuItems(menuItems.filter(item => item.id !== id))
      }
    } catch (error) {
      console.error('Failed to delete menu item:', error)
    }
  }

  // Add new state for selected items
  const [selectedItems, setSelectedItems] = useState<MenuItem[]>([])

  // Add function to handle item selection
  const toggleItemSelection = (item: MenuItem) => {
    setSelectedItems(prev => {
      const isSelected = prev.some(i => i.id === item.id)
      if (isSelected) {
        return prev.filter(i => i.id !== item.id)
      }
      return [...prev, item]
    })
  }

  // Modify handleAddToCart to handle multiple items
  const handleAddToCart = async () => {
    try {
      // Store all selected items in localStorage
      const cartItems = selectedItems.map(item => ({
        ...item,
        menuItemId: item.id,
        quantity: 1
      }))
      localStorage.setItem('selectedMenuItems', JSON.stringify(cartItems))
      
      // Redirect to the order page
      router.push('/orders/add')
    } catch (error) {
      console.error('Failed to add items to cart:', error)
    }
  }

  return (
    <div className="min-h-screen p-8 bg-background">
      <div className="max-w-7xl mx-auto space-y-6">
        <div className="flex flex-col gap-2">
          <motion.h1
            className="text-3xl font-semibold tracking-tight"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            Menu Items
          </motion.h1>
          <motion.p
            className="text-muted-foreground"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
          >
            Manage your restaurant menu items
          </motion.p>
        </div>

        <div className="flex justify-between items-center">
          <div className="flex items-center gap-2">
            <Input
              placeholder="Search menu items..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-64"
            />
            <Button variant="outline" onClick={handleSearch}>
              <RiSearchLine className="h-4 w-4" />
            </Button>
          </div>
          <div className="flex gap-2">
            {selectedItems.length > 0 && (
              <Button onClick={handleAddToCart} variant="default">
                <RiShoppingCartLine className="h-4 w-4 mr-2" />
                Add {selectedItems.length} to Cart
              </Button>
            )}
            <Button onClick={() => router.push('/menu/add')}>
              <RiAddLine className="h-4 w-4 mr-2" />
              Add Item
            </Button>
          </div>
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
          </div>
        ) : menuItems.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            No menu items found
          </div>
        ) : (
          <motion.div 
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
          >
            {menuItems.map((item) => (
              <motion.div
                key={item.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
              >
                <Card className="overflow-hidden hover:shadow-lg transition-shadow">
                  <div className="relative h-48 overflow-hidden">
                    <img
                      src={item.imageUrl}
                      alt={item.name}
                      className="w-full h-full object-cover"
                    />
                    {item.isSpecial && (
                      <Badge className="absolute top-2 right-2 bg-yellow-500">
                        Special
                      </Badge>
                    )}
                  </div>
                  <CardHeader>
                    <div className="flex justify-between items-start">
                      <div>
                        <CardTitle>{item.name}</CardTitle>
                        <CardDescription className="mt-1">{item.category}</CardDescription>
                      </div>
                      <Badge variant={item.available ? "success" : "destructive"}>
                        {item.available ? 'Available' : 'Unavailable'}
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <p className="text-sm text-muted-foreground mb-4">{item.description}</p>
                    <div className="flex justify-between items-center">
                      <div>
                        <p className="font-semibold text-lg">${item.price.toFixed(2)}</p>
                        <p className="text-sm text-muted-foreground">{item.prepTime} mins prep time</p>
                      </div>
                      <div className="flex gap-2">
                        <Button
                          variant={selectedItems.some(i => i.id === item.id) ? "default" : "outline"}
                          size="sm"
                          onClick={() => toggleItemSelection(item)}
                          className="text-primary hover:text-primary-dark"
                        >
                          {selectedItems.some(i => i.id === item.id) ? 'Selected' : 'Select'}
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => router.push(`/menu/${item.id}/edit`)}
                        >
                          <RiEditLine className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(item.id)}
                          className="text-red-500 hover:text-red-700"
                        >
                          <RiDeleteBinLine className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </motion.div>
            ))}
          </motion.div>
        )}
      </div>
    </div>
  )
}