import { Link } from "@heroui/link";
import { button as buttonStyles } from "@heroui/theme";
import { subtitle } from "@/components/primitives";
import DefaultLayout from "@/layouts/default";
import Footer from "@/components/Footer";

export default function IndexPage() {
  return (
    <DefaultLayout>
      <section className="flex flex-col items-center justify-center gap-4 py-8 md:py-10">
        <div className="inline-block max-w-lg text-center justify-center">
          <h1 className="text-6xl font-bold tracking-tight">
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-blue-500 to-violet-500 animate-gradient">
              Приветствую <br />
              в месте, где пицца
              <br />
              это не просто еда
            </span>
          </h1>
          <div className={subtitle({ class: "mt-4" })}>
            Откройте для себя мир вкуса
          </div>
        </div>

        <div className="flex gap-3">
          <Link
            href="/pizzas"
            className={buttonStyles({
              color: "primary",
              radius: "full",
              variant: "shadow",
            })}
          >
            Пиццы
          </Link>
          <Link
            href="/orders"
            className={buttonStyles({ variant: "bordered", radius: "full" })}
          >
            Заказы
          </Link>
        </div>
      </section>
      <Footer />
    </DefaultLayout>
  );
}
