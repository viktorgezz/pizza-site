import { title } from "@/components/primitives";
import DefaultLayout from "@/layouts/default";

export default function AboutPage() {
  return (
    <DefaultLayout>
      <section className="flex flex-col items-center justify-center gap-6 py-12 md:py-16">
        <h1 className={title()}>О нас</h1>
        <div className="max-w-2xl w-full p-8 bg-default-100 shadow-lg rounded-xl">
          <p className="text-lg leading-relaxed text-default-700 whitespace-pre-line">
            {`Добро пожаловать в «ПИЦЦКУ» — место, где пицца становится не просто едой, 
а настоящим проявлением заботы, тепла и радости!

Наш бренд — это про любовь к жизни, уютные моменты с близкими и, конечно, невероятно вкусную пиццу, которая заставляет улыбаться с первого укуса.

Наши пиццы — это взрыв вкуса! Хрустящее тесто, идеально сбалансированные соусы и щедрые порции начинки — мы не экономим на удовольствии.

Приходите в «ПИЦЦКУ» или закажите доставку, чтобы почувствовать нашу любовь, милоту и вкусность! 
С нами каждый день становится чуточку вкуснее!`}
          </p>
        </div>
      </section>
    </DefaultLayout>
  );
}
