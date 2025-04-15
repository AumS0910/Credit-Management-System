"use client"

import React, { useState } from "react"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { motion, AnimatePresence } from "framer-motion"
import {
  RiHome5Line, RiRestaurant2Line, RiBook2Line,
  RiShoppingBag3Line, RiLineChartLine, RiSettings4Line,
  RiMenu4Line, RiCloseLine, RiUser3Line
} from "react-icons/ri"
import { Button } from "@/components/ui/button"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Separator } from "@/components/ui/separator"
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet"

const sidebarItems = [
  {
    title: "Dashboard",
    icon: RiHome5Line,
    href: "/dashboard",
  },
  {
    title: "Customer",
    icon: RiUser3Line,
    href: "/customers/list",
  },
  {
    title: "Menu Management",
    icon: RiBook2Line,
    href: "/menu/list",  // Updated from "/menu-items/list" to "/menu/list"
  },
  {
    title: "Orders",
    icon: RiShoppingBag3Line,
    href: "/orders/list",
  },
  {
    title: "Analytics",
    icon: RiLineChartLine,
    href: "/analytics",
  },
  {
    title: "Settings",
    icon: RiSettings4Line,
    href: "/dashboard/settings",
  },
]



type SidebarProps = {
  className?: string;
};



export function Sidebar() {
  const pathname = usePathname()
  const [isOpen, setIsOpen] = useState(true)
  const [isMobileOpen, setIsMobileOpen] = useState(false)

  // Function to determine if a sidebar item is active
  const isActive = (href: string) => pathname === href

  // Desktop sidebar
  const DesktopSidebar = () => (
    <div
      className={`hidden md:flex flex-col border-r bg-background h-screen transition-all duration-300 ease-in-out ${
        isOpen ? "w-64" : "w-20"
      }`}
    >
      <div className="sticky top-0 z-10 flex h-16 items-center justify-between px-4 bg-background/80 backdrop-blur">
        <AnimatePresence initial={false}>
          {isOpen ? (
            <motion.div
              key="full-logo"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="flex items-center gap-2"
            >
              <span className="h-8 w-8 rounded-md bg-primary flex items-center justify-center">
                <RiRestaurant2Line className="h-5 w-5 text-white" />
              </span>
              <span className="font-semibold text-lg">Resto</span>
            </motion.div>
          ) : (
            <motion.div
              key="icon-logo"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="flex items-center"
            >
              <span className="h-8 w-8 rounded-md bg-primary flex items-center justify-center">
                <RiRestaurant2Line className="h-5 w-5 text-white" />
              </span>
            </motion.div>
          )}
        </AnimatePresence>
        <Button
          variant="ghost"
          size="icon"
          onClick={() => setIsOpen(!isOpen)}
          className="h-8 w-8"
        >
          <RiMenu4Line className="h-5 w-5" />
        </Button>
      </div>
      <ScrollArea className="flex-1 px-3 py-4">
        <nav className="flex flex-col gap-1">
          {sidebarItems.map((item, index) => (
            <Link
              key={item.href}
              href={item.href}
              className={`sidebar-item ${isActive(item.href) ? 'active' : ''}`}
            >
              <item.icon className="h-5 w-5" />
              <AnimatePresence initial={false}>
                {isOpen && (
                  <motion.span
                    key={`text-${item.href}`}
                    initial={{ opacity: 0, width: 0 }}
                    animate={{ opacity: 1, width: "auto" }}
                    exit={{ opacity: 0, width: 0 }}
                    transition={{ duration: 0.2 }}
                  >
                    {item.title}
                  </motion.span>
                )}
              </AnimatePresence>
            </Link>
          ))}
        </nav>
      </ScrollArea>
      <div className="mt-auto p-4">
        <Separator className="mb-4" />
        <div className="flex items-center gap-2">
          <div className="h-8 w-8 rounded-full bg-primary/20 flex items-center justify-center">
            <span className="text-sm font-medium text-primary">JD</span>
          </div>
          <AnimatePresence initial={false}>
            {isOpen && (
              <motion.div
                key="user-info"
                initial={{ opacity: 0, width: 0 }}
                animate={{ opacity: 1, width: "auto" }}
                exit={{ opacity: 0, width: 0 }}
                transition={{ duration: 0.2 }}
                className="overflow-hidden"
              >
                <div className="font-medium">John Doe</div>
                <div className="text-xs text-muted-foreground">Manager</div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  )

  // Mobile sidebar (using Sheet component)
  const MobileSidebar = () => (
    <div className="md:hidden flex items-center h-16 px-4 border-b bg-background/80 backdrop-blur sticky top-0 z-10">
      <Sheet open={isMobileOpen} onOpenChange={setIsMobileOpen}>
        <SheetTrigger asChild>
          <Button variant="ghost" size="icon" className="h-9 w-9">
            <RiMenu4Line className="h-6 w-6" />
          </Button>
        </SheetTrigger>
        <SheetContent side="left" className="w-64 p-0">
          <div className="flex h-16 items-center px-4 border-b">
            <div className="flex items-center gap-2">
              <span className="h-8 w-8 rounded-md bg-primary flex items-center justify-center">
                <RiRestaurant2Line className="h-5 w-5 text-white" />
              </span>
              <span className="font-semibold text-lg">Resto</span>
            </div>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => setIsMobileOpen(false)}
              className="h-8 w-8 ml-auto"
            >
              <RiCloseLine className="h-5 w-5" />
            </Button>
          </div>
          <ScrollArea className="flex-1 h-[calc(100vh-4rem)] px-3 py-4">
            <nav className="flex flex-col gap-1">
              {sidebarItems.map((item) => (
                <Link
                  key={item.href}
                  href={item.href}
                  onClick={() => setIsMobileOpen(false)}
                  className={`sidebar-item ${isActive(item.href) ? 'active' : ''}`}
                >
                  <item.icon className="h-5 w-5" />
                  <span>{item.title}</span>
                </Link>
              ))}
            </nav>
          </ScrollArea>
          <div className="p-4 border-t">
            <div className="flex items-center gap-2">
              <div className="h-8 w-8 rounded-full bg-primary/20 flex items-center justify-center">
                <span className="text-sm font-medium text-primary">JD</span>
              </div>
              <div>
                <div className="font-medium">John Doe</div>
                <div className="text-xs text-muted-foreground">Manager</div>
              </div>
            </div>
          </div>
        </SheetContent>
      </Sheet>
      <div className="ml-4 flex items-center gap-2">
        <span className="h-8 w-8 rounded-md bg-primary flex items-center justify-center">
          <RiRestaurant2Line className="h-5 w-5 text-white" />
        </span>
        <span className="font-semibold text-lg">Resto</span>
      </div>
    </div>
  )

  return (
    <>
      <DesktopSidebar />
      <MobileSidebar />
    </>
  )
}
