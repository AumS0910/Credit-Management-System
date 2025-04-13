import React from "react"
import { RiUserLine } from "react-icons/ri"

interface Customer {
  id: number;
  name: string;
  creditBalance: number;
  lastVisit: string;
}

interface RecentCustomersProps {
  customers: Customer[];
}

export function RecentCustomers({ customers }: RecentCustomersProps) {
  return (
    <div className="space-y-4">
      {customers.map((customer) => (
        <div
          key={customer.id}
          className="flex items-center justify-between p-4 rounded-lg bg-card/50"
        >
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-full bg-primary/10">
              <RiUserLine className="h-5 w-5 text-primary" />
            </div>
            <div>
              <p className="font-medium">{customer.name}</p>
              <p className="text-sm text-muted-foreground">
                Last visit: {new Date(customer.lastVisit).toLocaleDateString()}
              </p>
            </div>
          </div>
          <div className="text-right">
            <p className="font-medium">${customer.creditBalance.toFixed(2)}</p>
            <p className="text-sm text-muted-foreground">Credit Balance</p>
          </div>
        </div>
      ))}
      {customers.length === 0 && (
        <div className="text-center text-muted-foreground py-8">
          No recent customers
        </div>
      )}
    </div>
  )
}