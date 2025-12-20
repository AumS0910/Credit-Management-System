"use client"

import { useState, useEffect, useMemo } from "react"
import { useRouter } from "next/navigation"
import { motion, AnimatePresence } from "framer-motion"
import {
  RiArrowLeftLine,
  RiArrowRightLine,
  RiPlayCircleLine,
  RiEditLine,
  RiDeleteBinLine,
  RiSearchLine,
  RiAddLine,
  RiShoppingBag3Line
} from "react-icons/ri"
import { Button } from "@/components/ui/button"
import useMeasure from 'react-use-measure'
import { Input } from "@/components/ui/input"
import { useSpring, animated } from '@react-spring/web'
import { Sidebar } from "@/components/sidebar" // Import Sidebar component
import { getApiUrl } from "@/lib/api"

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
      const response = await fetch(getApiUrl(`/menu-items`), {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Admin-ID': id.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        const itemsWithImages = await Promise.all(data.map(async (item) => {
          const imageResponse = await fetch(getApiUrl(`/pexels/food-image?name=${encodeURIComponent(item.name)}`))
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
      const response = await fetch(getApiUrl(`/menu-items/search?query=${searchQuery}`))
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
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id: adminId } = JSON.parse(adminData)

      const response = await fetch(getApiUrl(`/menu-items/${id}`), {
        method: 'DELETE',
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": adminId.toString(),
          "Authorization": `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.ok) {
        setMenuItems(menuItems.filter(item => item.id !== id))
        // Reset activeIndex if necessary
        if (activeIndex >= menuItems.length - 1) {
          setActiveIndex(Math.max(0, menuItems.length - 2))
        }
      } else {
        console.error('Failed to delete menu item')
      }
    } catch (error) {
      console.error('Failed to delete menu item:', error)
    }
  }

  const [selectedItems, setSelectedItems] = useState<MenuItem[]>([])

  const toggleItemSelection = (item: MenuItem) => {
    setSelectedItems(prev => {
      const isSelected = prev.some(i => i.id === item.id)
      if (isSelected) {
        return prev.filter(i => i.id !== item.id)
      }
      return [...prev, item]
    })
  }

  const handleAddToCart = async () => {
    try {
      const cartItems = selectedItems.map(item => ({
        ...item,
        menuItemId: item.id,
        quantity: 1
      }))
      localStorage.setItem('selectedMenuItems', JSON.stringify(cartItems))
      router.push('/orders/add')
    } catch (error) {
      console.error('Failed to add items to cart:', error)
    }
  }

  const [ref, bounds] = useMeasure()
  const [active, setActive] = useState(0)
  
  const getPositions = useMemo(() => (index: number) => {
    const r = Math.sqrt(Math.pow(bounds.width, 2) + Math.pow(bounds.height, 2)) / 2
    const theta = (2 * Math.PI) / Math.min(menuItems.length, 8)
    const x = r * Math.cos(theta * index - active * theta)
    const z = r * Math.sin(theta * index - active * theta)
    const scale = (z + r) / (2 * r)
    return { x, z, scale }
  }, [bounds.width, bounds.height, active, menuItems.length])

  const [activeIndex, setActiveIndex] = useState(0)
  
  const nextSlide = () => {
    setActiveIndex((prev) => (prev + 1) % menuItems.length)
  }
  
  const prevSlide = () => {
    setActiveIndex((prev) => (prev - 1 + menuItems.length) % menuItems.length)
  }
  
  const calculatePosition = (index: number) => {
    const radius = 400
    const totalItems = menuItems.length
    const angle = (2 * Math.PI * (index - activeIndex)) / totalItems
    
    return {
      x: radius * Math.cos(angle),
      z: radius * Math.sin(angle),
      scale: index === activeIndex ? 1 : 0.7,
      rotateY: (angle * 180) / Math.PI
    }
  }

  const handleEdit = (id: number) => {
    router.push(`/menu/edit/${id}`);
  };

  return (
    <div className="flex">
      <Sidebar /> {/* Add Sidebar component */}
      <div 
        className="flex-1 min-h-screen p-8"
        style={{
          backgroundImage: "url('/images/a3c2481af3cd74e561fa34522037f485.jpg')",
          backgroundSize: 'cover', // Changed to 'cover' to eliminate white space
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat'
          
        }}
      >
        <div className="flex flex-col items-center gap-8">
          <div className="w-full flex justify-between items-center">
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
            <Button onClick={() => router.push('/menu/add')}>
              <RiAddLine className="h-4 w-4 mr-2" />
              Add Item
            </Button>
          </div>
  
          <div className="carousel-container relative">
            <AnimatePresence mode="wait">
              {menuItems[activeIndex] && (
                <motion.div
                  key={menuItems[activeIndex].id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  transition={{ duration: 0.4, ease: "easeOut" }}
                  className="absolute left-12 top-8 w-[450px] z-20"
                >
                  <div className="flex flex-col">
                    <motion.div
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.1 }}
                      className="flex items-center gap-2 text-sm text-gray-600 mb-1"
                    >
                      <span>#{activeIndex + 1} {menuItems[activeIndex].popularity || 'Featured'} dish</span>
                    </motion.div>
                    
                    <div className="flex justify-between items-start">
                      <div className="flex flex-col overflow-hidden">
                        <motion.h2
                          key={menuItems[activeIndex].name}
                          initial={{ y: 40, opacity: 0 }}
                          animate={{ y: 0, opacity: 1 }}
                          exit={{ y: -40, opacity: 0 }}
                          transition={{ duration: 0.5, ease: "easeOut" }}
                          className="text-4xl font-light uppercase tracking-wide"
                        >
                          {menuItems[activeIndex].name}
                        </motion.h2>
                        <motion.h3
                          key={menuItems[activeIndex].category}
                          initial={{ y: 40, opacity: 0 }}
                          animate={{ y: 0, opacity: 1 }}
                          exit={{ y: -40, opacity: 0 }}
                          transition={{ duration: 0.5, delay: 0.1, ease: "easeOut" }}
                          className="text-3xl font-bold uppercase text-gray-700"
                        >
                          {menuItems[activeIndex].category || menuItems[activeIndex].type || ''}
                        </motion.h3>
                      </div>
                      {/* Removed rating badge */}
                    </div>
  
                    <motion.div
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.2 }}
                      className="flex gap-4 mt-4"
                    >
                      {selectedItems.length > 0 && (
                        <Button
                          onClick={handleAddToCart}
                          className="flex items-center gap-2 bg-primary text-white"
                        >
                          <RiShoppingBag3Line className="h-5 w-5" />
                          Order Selected ({selectedItems.length})
                        </Button>
                      )}
                    </motion.div>
  
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: 0.3 }}
                      className="mt-8 bg-white rounded-xl p-6"
                    >
                      <div className="flex items-start justify-between">
                        <div>
                          <h3 className="text-xl font-semibold">Overview</h3>
                          <p className="text-gray-600 mt-2">{menuItems[activeIndex].description}</p>
                        </div>
                      </div>
                      <motion.div
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.4 }}
                        className="flex gap-4 mt-6"
                      >
                        <Button 
                          variant={selectedItems.some(item => item.id === menuItems[activeIndex].id) ? "default" : "outline"}
                          onClick={() => toggleItemSelection(menuItems[activeIndex])}
                        >
                          {selectedItems.some(item => item.id === menuItems[activeIndex].id) ? 'Selected' : 'Select'}
                        </Button>
                        <Button 
                          variant="outline" 
                          onClick={() => handleEdit(menuItems[activeIndex].id)}
                        >
                          <RiEditLine className="h-4 w-4" />
                        </Button>
                        <Button variant="outline" onClick={() => handleDelete(menuItems[activeIndex].id)}>
                          <RiDeleteBinLine className="h-4 w-4" />
                        </Button>
                      </motion.div>
                    </motion.div>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>

            {/* Remove this entire floating button section */}
            {/* {selectedItems.length > 0 && (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="fixed bottom-8 right-8 z-50"
              >
                <Button
                  onClick={handleAddToCart}
                  className="flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-full shadow-lg"
                >
                  <RiShoppingBag3Line className="h-5 w-5" />
                  Order Selected ({selectedItems.length})
                </Button>
              </motion.div>
            )} */}

            <div className="carousel-track" />
            
            {menuItems.map((item, index) => {
              const rotation = ((index - activeIndex) * (180 / menuItems.length));
              const translateY = Math.cos((rotation * Math.PI) / 180) * 200;
              const translateX = Math.sin((rotation * Math.PI) / 180) * 400;
              const scale = index === activeIndex ? 1 : 0;
              const isVisible = Math.abs(index - activeIndex) <= 1;
    
              return (
                <motion.div
                  key={item.id}
                  className="carousel-item"
                  initial={false}
                  animate={{
                    x: translateX + 100,
                    y: -translateY + 120, // Added 50px to move down
                    scale,
                    opacity: isVisible ? 1 : 0,
                    zIndex: index === activeIndex ? 2 : 1,
                    rotateY: rotation,
                  }}
                  transition={{ 
                    duration: 0.6,
                    opacity: { duration: 0.3 }
                  }}
                  style={{
                    display: isVisible ? 'block' : 'none'
                  }}
                >
                  <div className="w-[300px] h-[300px] rounded-full overflow-hidden">
                    <img
                      src={item.imageUrl}
                      alt={item.name}
                      className="w-full h-full object-cover"
                    />
                  </div>
                </motion.div>
              );
            })}
          </div>
  
          <div className="thumbnail-slider relative">
            <button className="carousel-arrow left" onClick={prevSlide}>
              <RiArrowLeftLine className="h-6 w-6" />
            </button>
            <div className="flex gap-4 overflow-hidden w-[480px]"> {/* Fixed width container for 4 items */}
              {menuItems.slice(
                Math.floor(activeIndex / 4) * 4,
                Math.floor(activeIndex / 4) * 4 + 4
              ).map((item, index) => (
                <div 
                  key={item.id} 
                  className={`flex-shrink-0 flex flex-col items-center p-3 rounded-xl transition-all duration-300 cursor-pointer ${
                    index + Math.floor(activeIndex / 4) * 4 === activeIndex 
                      ? 'bg-gray-100/70 backdrop-blur-sm shadow-sm' 
                      : 'hover:bg-gray-50/50'
                  }`}
                  onClick={() => setActiveIndex(index + Math.floor(activeIndex / 4) * 4)}
                >
                  <div className={`w-20 h-20 overflow-hidden rounded-xl transition-transform duration-300 ${
                    index + Math.floor(activeIndex / 4) * 4 === activeIndex ? 'scale-105' : ''
                  }`}>
                    <img 
                      src={item.imageUrl} 
                      alt={item.name} 
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <span className="text-sm text-gray-600 mt-2 text-center line-clamp-1">
                    {item.name}
                  </span>
                </div>
              ))}
            </div>
            <button className="carousel-arrow right" onClick={nextSlide}>
              <RiArrowRightLine className="h-6 w-6" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}