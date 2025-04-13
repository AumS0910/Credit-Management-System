import { useEffect } from "react";
import { useRouter } from "next/router";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    // Redirect to the login page
    router.push("/login");
  }, [router]);

  return null; // No content needed as it redirects
}