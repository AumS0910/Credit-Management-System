import React from "react";

type HeaderProps = {
  className?: string;
};

export const Header: React.FC<HeaderProps> = ({ className, children }) => {
  return (
    <header className={`header ${className}`}>
      {children}
    </header>
  );
};
