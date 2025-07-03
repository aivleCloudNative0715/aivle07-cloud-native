import React from "react";
import { cva } from "class-variance-authority";
import { cn } from "lib/utils";

const buttonVariants = cva(
    "inline-flex items-center px-4 py-2 text-sm font-medium transition-colors rounded-md",
    {
        variants: {
            variant: {
                default: "bg-sky-500 text-white hover:bg-sky-100 hover:text-gray-900",
                secondary: "bg-sky-700 text-white hover:bg-gray-300",
                ghost: "bg-transparent hover:bg-gray-100 hover:text-gray-900",
            },
        },
        defaultVariants: { variant: "default" },
    }
);

export const Button = React.forwardRef(function Button(
    { className, variant = "default", ...props },
    ref
) {
    return (
        <button
            ref={ref}
            className={cn(buttonVariants({ variant }), className)}
            {...props}
        />
    );
});
