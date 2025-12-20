"use client"

import React, { useState } from "react"
import { motion } from "framer-motion"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { RiRestaurantLine, RiPriceTag3Line, RiTimeLine, RiImageAddLine } from "react-icons/ri"
import { getApiUrl } from "@/lib/api"

interface MenuItem {
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  prepTime: number;
  isSpecial: boolean;
  available: boolean;
  rating: string;
}

export default function AddMenuItemPage() {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [imagePreview, setImagePreview] = useState<string | null>(null)

  const [formData, setFormData] = useState<MenuItem>({
    name: "",
    description: "",
    price: 0,
    category: "",
    imageUrl: "",
    prepTime: 0,
    isSpecial: false,
    available: true,
    rating: "5"
  })

  const categories = [
    "Appetizers",
    "Main Course",
    "Desserts",
    "Beverages",
    "Specials"
  ]

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      const reader = new FileReader()
      reader.onloadend = () => {
        setImagePreview(reader.result as string)
        setFormData({ ...formData, imageUrl: file.name })
      }
      reader.readAsDataURL(file)
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
      const response = await fetch(getApiUrl("/menu-items/add"), {  // Update API endpoint
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem('token')}`,
          "Admin-ID": id.toString()
        },
        body: JSON.stringify({
          ...formData,
          adminId: id
        })
      })

      if (response.ok) {
        router.push('/menu/list')
      } else {
        const data = await response.json()
        setError(data.error || "Failed to add menu item")
      }
    } catch (error) {
      setError("An error occurred. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen p-8 bg-background relative">
      <div 
        className="absolute inset-0 z-0"
        style={{
          backgroundImage: "url('/images/restaurant 1.jpg')",
          backgroundSize: "cover",
          backgroundPosition: "center",
          backgroundRepeat: "no-repeat",
          opacity: 0.15,
          filter: "blur(8px)"
        }}
      />
      
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-4xl mx-auto space-y-8 relative z-10"
      >
        <div className="flex flex-col gap-2">
          <motion.h1
            className="text-3xl font-semibold tracking-tight"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            Add New Menu Item
          </motion.h1>
          <motion.p
            className="text-muted-foreground"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            Create a new dish or beverage for your menu
          </motion.p>
        </div>

        <Card className="glass-card">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <RiRestaurantLine className="h-6 w-6 text-primary" />
              Item Details
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="name">Item Name</Label>
                  <Input
                    id="name"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="category">Category</Label>
                  <Select
                    id="category"
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    required
                  >
                    <option value="">Select category</option>
                    {categories.map((cat) => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="price">Price</Label>
                  <div className="relative">
                    <RiPriceTag3Line className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                    <Input
                      id="price"
                      type="number"
                      step="0.01"
                      min="0"
                      className="pl-10"
                      value={formData.price.toString()}
                      onChange={(e) => setFormData({ ...formData, price: e.target.value === '' ? 0 : parseFloat(e.target.value) })}
                      required
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="prepTime">Preparation Time (minutes)</Label>
                  <div className="relative">
                    <RiTimeLine className="absolute left-3 top-3 h-5 w-5 text-muted-foreground" />
                    <Input
                      id="prepTime"
                      type="number"
                      min="0"
                      className="pl-10"
                      value={formData.prepTime.toString()}
                      onChange={(e) => setFormData({ ...formData, prepTime: e.target.value === '' ? 0 : parseInt(e.target.value) })}
                      required
                    />
                  </div>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="description">Description</Label>
                <Textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  required
                />
              </div>

              

              <div className="flex flex-wrap gap-6">
                <div className="flex items-center space-x-2">
                  <Switch
                    id="isSpecial"
                    checked={formData.isSpecial}
                    onCheckedChange={(checked) => setFormData({ ...formData, isSpecial: checked })}
                  />
                  <Label htmlFor="isSpecial">Special Item</Label>
                </div>

                <div className="flex items-center space-x-2">
                  <Switch
                    id="available"
                    checked={formData.available}
                    onCheckedChange={(checked) => setFormData({ ...formData, available: checked })}
                  />
                  <Label htmlFor="available">Available</Label>
                </div>
              </div>

              {error && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="text-red-500 text-sm"
                >
                  {error}
                </motion.div>
              )}

              <div className="flex gap-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.back()}
                  className="flex-1"
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  className="flex-1"
                  disabled={loading}
                >
                  {loading ? "Adding..." : "Add Item"}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  )
}