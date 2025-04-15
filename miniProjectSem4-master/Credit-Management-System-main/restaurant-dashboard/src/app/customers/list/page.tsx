"use client"

import React, { useEffect, useState } from "react"
import { motion } from "framer-motion"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { RiUserLine, RiSearchLine, RiAddLine, RiEditLine, RiDeleteBinLine } from "react-icons/ri"

interface Customer {
  id: number;
  name: string;
  phone: string;
  email: string;
  address: string;
  totalCredit: number;
  creditBalance: number;
  active: boolean;
}

export default function CustomerListPage() {
  const router = useRouter()
  const [customers, setCustomers] = useState<Customer[]>([])
  const [loading, setLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    fetchCustomers()
  }, [])

  const fetchCustomers = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id } = JSON.parse(adminData)
      const response = await fetch(`http://localhost:8080/customers`, {
        headers: {
          'Admin-ID': id.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        setCustomers(data)
      }
    } catch (error) {
      console.error('Failed to fetch customers:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      fetchCustomers()
      return
    }

    try {
      const response = await fetch(`http://localhost:8080/customers/search?query=${searchQuery}`)
      if (response.ok) {
        const data = await response.json()
        setCustomers(data)
      }
    } catch (error) {
      console.error('Failed to search customers:', error)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this customer?')) return

    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id: adminId } = JSON.parse(adminData)
      const response = await fetch(`http://localhost:8080/customers/${id}/delete`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Admin-ID': adminId.toString()
        }
      })

      if (response.ok) {
        setCustomers(customers.filter(customer => customer.id !== id))
        alert('Customer deleted successfully')
      } else {
        const text = await response.text()
        throw new Error(text || 'Failed to delete customer')
      }
    } catch (error) {
      console.error('Failed to delete customer:', error)
      alert(error instanceof Error ? error.message : 'Failed to delete customer')
    }
  }

  const handleSettleBalance = async (customerId: number) => {
    const customer = customers.find(c => c.id === customerId)
    if (!customer) return

    const amount = prompt(`Enter settlement amount (max: $${customer.creditBalance.toFixed(2)})`)
    if (!amount) return

    const settlementAmount = parseFloat(amount)
    if (isNaN(settlementAmount) || settlementAmount <= 0 || settlementAmount > customer.creditBalance) {
      alert('Invalid settlement amount')
      return
    }

    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id: adminId } = JSON.parse(adminData)
      const response = await fetch(`http://localhost:8080/customers/${customerId}/settle`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Admin-ID': adminId.toString()
        },
        body: JSON.stringify({
          amount: settlementAmount,
          customerId: customerId,
          adminId: adminId,
          type: 'SETTLEMENT',
          status: 'COMPLETED',
          notes: `Settled ${settlementAmount.toFixed(2)} from total balance of ${customer.creditBalance.toFixed(2)}`,
          transactionDate: new Date().toISOString()
        })
      })

      if (response.ok) {
        const updatedCustomer = await response.json()
        setCustomers(customers.map(c => 
          c.id === customerId ? updatedCustomer : c
        ))
        alert(`Successfully settled $${settlementAmount.toFixed(2)}`)
      } else {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Failed to settle balance')
      }
    } catch (error) {
      console.error('Failed to settle balance:', error)
      alert(error instanceof Error ? error.message : 'Failed to settle balance')
    }
  }

  const handleEdit = async (customerId: number) => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id: adminId } = JSON.parse(adminData)
      // First fetch the customer data
      const response = await fetch(`http://localhost:8080/customers/${customerId}`, {
        headers: {
          'Admin-ID': adminId.toString()
        }
      })

      if (response.ok) {
        router.push(`/customers/edit/${customerId}`)
      } else {
        throw new Error('Failed to fetch customer data')
      }
    } catch (error) {
      console.error('Failed to navigate to edit page:', error)
      alert('Failed to open edit page')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2">
        <motion.h1
          className="text-3xl font-semibold tracking-tight"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          Customers
        </motion.h1>
        <motion.p
          className="text-muted-foreground"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          Manage your customer profiles and credit balances
        </motion.p>
      </div>

      <Card className="glass-card">
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <RiUserLine className="h-6 w-6 text-primary" />
            Customer List
          </CardTitle>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <Input
                placeholder="Search customers..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-64"
              />
              <Button variant="outline" onClick={handleSearch}>
                <RiSearchLine className="h-4 w-4" />
              </Button>
            </div>
            <Button onClick={() => router.push('/customers/add')}>
              <RiAddLine className="h-4 w-4 mr-2" />
              Add Customer
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="relative overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead className="text-xs uppercase bg-background/50">
                <tr>
                  <th className="px-6 py-3">Name</th>
                  <th className="px-6 py-3">Contact</th>
                  <th className="px-6 py-3">Credit Limit</th>
                  <th className="px-6 py-3">Balance</th>
                  <th className="px-6 py-3">Status</th>
                  <th className="px-6 py-3">Actions</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((customer) => (
                  <motion.tr
                    key={customer.id}
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="border-b border-background/20"
                  >
                    <td className="px-6 py-4 font-medium">{customer.name}</td>
                    <td className="px-6 py-4">
                      {customer.phone}<br />
                      <span className="text-xs text-muted-foreground">{customer.email}</span>
                    </td>
                    <td className="px-6 py-4">${customer.totalCredit.toFixed(2)}</td>
                    <td className="px-6 py-4">${customer.creditBalance.toFixed(2)}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded-full text-xs ${
                        customer.active ? 'bg-green-500/20 text-green-500' : 'bg-red-500/20 text-red-500'
                      }`}>
                        {customer.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEdit(customer.id)}
                        >
                          <RiEditLine className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(customer.id)}
                          className="text-red-500 hover:text-red-700"
                        >
                          <RiDeleteBinLine className="h-4 w-4" />
                        </Button>
                        {customer.creditBalance > 0 && (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleSettleBalance(customer.id)}
                            className="text-green-500 hover:text-green-700"
                          >
                            Settle
                          </Button>
                        )}
                      </div>
                    </td>
                  </motion.tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}