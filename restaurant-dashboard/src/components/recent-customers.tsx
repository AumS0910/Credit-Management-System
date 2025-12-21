import React from "react"
import { RiUserLine } from "react-icons/ri"

interface Customer {
  id: string;
  name: string;
  creditBalance: number;
  createdAt?: string;
  lastVisit?: string;
}

interface RecentCustomersProps {
  customers: Customer[];
}

export function RecentCustomers({ customers }: RecentCustomersProps) {
  return (
    <div className="space-y-4">
      {customers && Array.isArray(customers) && customers.length > 0 ? (
        customers
          .filter(customer => customer && typeof customer === 'object' && customer.name && customer.id)
          .map((customer) => (
            <div
              key={customer.id}
              className="flex items-center justify-between p-4 rounded-lg bg-card/50"
            >
              <div className="flex items-center gap-3">
                <div className="p-2 rounded-full bg-primary/10">
                  <RiUserLine className="h-5 w-5 text-primary" />
                </div>
                <div>
                  <p className="font-medium">{customer.name || 'Unknown Customer'}</p>
                  <p className="text-sm text-muted-foreground">
                    Created: {customer.createdAt ? new Date(customer.createdAt).toLocaleDateString() : 'N/A'}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <p className="font-medium">${typeof customer.creditBalance === 'number' ? customer.creditBalance.toFixed(2) : '0.00'}</p>
                <p className="text-sm text-muted-foreground">Credit Balance</p>
              </div>
            </div>
          ))
      ) : (
        <div className="text-center text-muted-foreground py-8">
          No recent customers
        </div>
      )}
    </div>
  )
}