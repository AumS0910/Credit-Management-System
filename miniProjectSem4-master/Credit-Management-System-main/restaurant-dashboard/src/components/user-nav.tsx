"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { RiLogoutBoxLine, RiUser3Line, RiSettings3Line } from "react-icons/ri"

export function UserNav() {
  const router = useRouter()
  const [adminData, setAdminData] = useState<{ name: string; username: string } | null>(null)

  useEffect(() => {
    // Get admin data from localStorage on component mount
    const data = localStorage.getItem('adminData')
    if (data) {
      const parsedData = JSON.parse(data)
      setAdminData({
        name: parsedData.name, // Using username as name since that's what we store
        username: parsedData.username
      })
    }
  }, [])

  const handleLogout = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/logout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem('token')}`
        },
      })

      // Always clear local storage and redirect, even if the server request fails
      localStorage.removeItem('adminData')
      localStorage.removeItem('token')
      router.push('/')
      
    } catch (error) {
      // Even if the server request fails, we still want to log out locally
      localStorage.removeItem('adminData')
      localStorage.removeItem('token')
      router.push('/')
    }
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <button className="relative h-9 w-9 rounded-full bg-primary/10 ring-offset-background transition-opacity hover:opacity-80 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2">
          <Avatar className="h-9 w-9">
            <AvatarImage src="/avatar.png" alt={adminData?.username || 'User'} />
            <AvatarFallback className="bg-primary/20 text-primary">
              {adminData?.username?.charAt(0).toUpperCase() || 'U'}
            </AvatarFallback>
          </Avatar>
        </button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56 glass">
        <DropdownMenuLabel>
          <div className="flex flex-col space-y-1">
            <p className="text-sm font-medium leading-none">
              {adminData?.username || 'Loading...'}
            </p>
            <p className="text-xs leading-none text-muted-foreground">
              {adminData?.username || 'loading...'}
            </p>
          </div>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuItem className="cursor-pointer">
          <RiUser3Line className="mr-2 h-4 w-4" />
          <span>Profile</span>
        </DropdownMenuItem>
        <DropdownMenuItem className="cursor-pointer">
          <RiSettings3Line className="mr-2 h-4 w-4" />
          <span>Settings</span>
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem 
          className="cursor-pointer text-destructive focus:text-destructive"
          onClick={handleLogout}
        >
          <RiLogoutBoxLine className="mr-2 h-4 w-4" />
          <span>Log out</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
