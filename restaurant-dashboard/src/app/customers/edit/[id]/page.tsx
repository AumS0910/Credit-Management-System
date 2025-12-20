"use client"

import React, { useEffect, useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { RiUserLine } from "react-icons/ri"
import { getApiUrl } from "@/lib/api"

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

export default function EditCustomerPage() {
  const router = useRouter()
  const params = useParams()
  const [customer, setCustomer] = useState<Customer | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchCustomer()
  }, [])

  const fetchCustomer = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id: adminId } = JSON.parse(adminData)
      const response = await fetch(getApiUrl(`customers/${params.id}`), {
        headers: {
          'Admin-ID': adminId.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        setCustomer(data)
      }
    } catch (error) {
      console.error('Failed to fetch customer:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }

      const { id: adminId } = JSON.parse(adminData)
      const response = await fetch(getApiUrl(`/customers/${params.id}/update`), {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Admin-ID': adminId.toString()
        },
        body: JSON.stringify({
          name: customer?.name,
          phone: customer?.phone,
          email: customer?.email,
          address: customer?.address,
          totalCredit: customer?.totalCredit
        })
      })

      if (response.ok) {
        router.push('/customers/list')
      } else {
        const errorData = await response.text()
        alert(errorData || 'Failed to update customer')
      }
    } catch (error) {
      console.error('Failed to update customer:', error)
      alert('Failed to update customer')
    }
  }

  if (loading) return <div>Loading...</div>
  if (!customer) return <div>Customer not found</div>

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <RiUserLine className="h-6 w-6 text-primary" />
            Edit Customer
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Input
                placeholder="Name"
                value={customer.name}
                onChange={(e) => setCustomer({ ...customer, name: e.target.value })}
              />
            </div>
            <div>
              <Input
                placeholder="Phone"
                value={customer.phone}
                onChange={(e) => setCustomer({ ...customer, phone: e.target.value })}
              />
            </div>
            <div>
              <Input
                placeholder="Email"
                value={customer.email}
                onChange={(e) => setCustomer({ ...customer, email: e.target.value })}
              />
            </div>
            <div>
              <Input
                placeholder="Address"
                value={customer.address}
                onChange={(e) => setCustomer({ ...customer, address: e.target.value })}
              />
            </div>
            <div>
              <Input
                type="number"
                placeholder="Credit Limit"
                value={customer.totalCredit}
                onChange={(e) => setCustomer({ ...customer, totalCredit: parseFloat(e.target.value) })}
              />
            </div>
            <div className="flex justify-end gap-4">
              <Button variant="outline" onClick={() => router.back()}>
                Cancel
              </Button>
              <Button type="submit">
                Save Changes
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}