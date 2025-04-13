"use client"

import type React from "react"
import { Sidebar } from "@/components/sidebar"
import { Header } from "@/components/header"
import { ModeToggle } from "@/components/mode-toggle"
import { UserNav } from "@/components/user-nav"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="flex h-screen overflow-hidden">
      <Sidebar />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header>
          <div className="flex flex-1 items-center justify-between space-x-2">
            <div className="flex items-center" /> {/* Empty flex div with proper self-closing tag */}
            <div className="flex items-center space-x-2">
              <ModeToggle />
              <UserNav />
            </div>
          </div>
        </Header>
        <main className="flex-1 overflow-auto p-4 md:p-6">
          {children}
        </main>
      </div>
    </div>
  )
}
